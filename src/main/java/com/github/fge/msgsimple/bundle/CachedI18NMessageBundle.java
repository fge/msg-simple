package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Hopefully, this class is...
@ThreadSafe
public abstract class CachedI18NMessageBundle
    extends I18NMessageBundle
{
    /**
     * Set of locales known to have failed lookup.
     *
     * <p>When a locale is in this set, it will not attempt to be reloaded.</p>
     */
    private final Set<Locale> lookupFailures
        = new CopyOnWriteArraySet<Locale>();
    /**
     * Map pairing locales with {@link FutureTask} instances returning message
     * sources
     *
     * <p>There will only ever be one task associated with one locale; we
     * therefore choose to make it a normal map, guarded by a {@link
     * ReentrantLock}.</p>
     *
     * <p>The tasks' {@link FutureTask#run()} method will be executed the first
     * time this object is initialized.</p>
     */
    @GuardedBy("lock")
    private final Map<Locale, FutureTask<MessageSource>> lookups
        = new HashMap<Locale, FutureTask<MessageSource>>();

    /**
     * Lock used to guarantee exclusive access to the {@link #lookups} map
     */
    private final Lock lock = new ReentrantLock();

    @Override
    protected final List<MessageSource> getSources(final Locale locale)
    {
        /*
         * Check whether the lookup has been declared to fail already. If this
         * is the case, just return an empty list.
         */
        if (lookupFailures.contains(locale))
            return Collections.emptyList();

        FutureTask<MessageSource> task;

        /*
         * If we reach this point, we have a potential candidate message source.
         *
         * Grab an exclusive lock to the lookups map.
         */
        lock.lock();
        try {
            /*
             * Try and see whether there is already a FutureTask associated with
             * this locale.
             */
            task = lookups.get(locale);
            if (task == null) {
                /*
                 * If not, create it and run it.
                 */
                task = new FutureTask<MessageSource>(tryLocale(locale));
                lookups.put(locale, task);
                task.run();
            }
        } finally {
            lock.unlock();
        }

        /*
         * Try and get the result for this locale; on any failure event (either
         * an IOException thrown by tryAndLookup() or a thread interrupt),
         * record the failure into the (thread safe) lookupFailures set, and
         * return an empty list.
         */
        try {
            return Arrays.asList(task.get());
        } catch (ExecutionException ignored) {
            lookupFailures.add(locale);
            return Collections.emptyList();
        } catch (InterruptedException  ignored) {
            lookupFailures.add(locale);
            return Collections.emptyList();
        }
    }

    protected abstract MessageSource tryAndLookup(final Locale locale)
        throws IOException;

    @Override
    public final Builder modify()
    {
        throw new IllegalStateException("cached bundles cannot be modified");
    }

    /**
     * Wraps an invocation of {@link #tryAndLookup(Locale)} into a {@link
     * Callable}
     *
     * @param locale the locale to pass as an argument to {@link
     * #tryAndLookup(Locale)}
     * @return a {@link Callable}
     */
    private Callable<MessageSource> tryLocale(final Locale locale)
    {
        return new Callable<MessageSource>()
        {
            @Override
            public MessageSource call()
                throws IOException
            {
                return tryAndLookup(locale);
            }
        };
    }
}
