package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

// Hopefully, this class is...
@ThreadSafe
public abstract class CachedI18NMessageBundle
    extends I18NMessageBundle
{
    private static final int NTHREADS = 5;

    private final ExecutorService service
        = Executors.newFixedThreadPool(NTHREADS);

    private final long duration;
    private final TimeUnit timeUnit;

    protected CachedI18NMessageBundle(final long duration,
        final TimeUnit timeUnit)
    {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    protected CachedI18NMessageBundle()
    {
        this(5L, TimeUnit.SECONDS);
    }

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
    private final Map<Locale, FutureTask<MessageSource>> lookups
        = new HashMap<Locale, FutureTask<MessageSource>>();

    @Override
    protected final List<MessageSource> getSources(final Locale locale)
    {
        FutureTask<MessageSource> task;

        /*
         * Grab an exclusive lock to the lookups map. The lock is held only for
         * the time necessary to grab the FutureTask or create it (and run it)
         * if it didn't exist previously.
         */
        synchronized (lookups) {
            /*
             * Try and see whether there is already a FutureTask associated with
             * this locale.
             */
            task = lookups.get(locale);
            if (task == null || task.isCancelled()) {
                /*
                 * If not, create it and run it.
                 */
                task = lookupTask(locale);
                lookups.put(locale, task);
                service.execute(task);
            }
        }

        /*
         * Try and get the result for this locale; on any failure event (either
         * an IOException thrown by tryAndLookup() or a thread interrupt),
         * return an empty list.
         */
        try {
            return Arrays.asList(task.get(duration, timeUnit));
        } catch (ExecutionException ignored) {
            return Collections.emptyList();
        } catch (InterruptedException ignored) {
            return Collections.emptyList();
        } catch (TimeoutException ignored) {
            task.cancel(true);
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
     * FutureTask}
     *
     * @param locale the locale to pass as an argument to {@link
     * #tryAndLookup(Locale)}
     * @return a {@link FutureTask}
     */
    private FutureTask<MessageSource> lookupTask(final Locale locale)
    {
        final Callable<MessageSource> callable = new Callable<MessageSource>()
        {
            @Override
            public MessageSource call()
                throws IOException
            {
                return tryAndLookup(locale);
            }
        };

        return new FutureTask<MessageSource>(callable);
    }
}
