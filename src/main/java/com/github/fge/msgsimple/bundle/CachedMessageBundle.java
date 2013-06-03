/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

/**
 * A {@link MessageBundle} with caching
 *
 * <p>Implementations of this class only have one method to override: {@link
 * #tryAndLookup(Locale)}.</p>
 *
 * <p>When a locale is looked up, this method is wrapped into a {@link
 * FutureTask} and the message source is returned as the result of this task.
 * </p>
 *
 * <p>Note that the task is only ever created once; this means lookup results,
 * whether they be successes or failures, are kept during the lifetime of the
 * bundle existence. The only exception is when a lookup task times out; in this
 * case, the task is {@link FutureTask#cancel(boolean)}led, evicted from the
 * task map, and a new task is recreated. The default lookup timeout is 5
 * seconds; you can choose another value at construction time.</p>
 *
 * <p>Internally, lookup tasks are recorded in a simple `Map`, accessed
 * synchronously; tasks are executed asynchronously in an {@link
 * ExecutorService} using {@link ExecutorService#execute(Runnable)}.</p>
 *
 * @see FutureTask
 * @see Executors#newFixedThreadPool(int)
 */
@ThreadSafe
public abstract class CachedMessageBundle
    extends MessageBundle
{
    private static final int NTHREADS = 5;

    private final ExecutorService service
        = Executors.newFixedThreadPool(NTHREADS);

    private final long duration;
    private final TimeUnit timeUnit;

    /**
     * Protected constructor
     *
     * @param duration number of units for the timeout
     * @param timeUnit time unit for the timeout
     */
    protected CachedMessageBundle(final long duration, final TimeUnit timeUnit)
    {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    /**
     * No-arg protected constructor
     *
     * <p>This calls {@link #CachedMessageBundle(long, TimeUnit)} with {@code
     * 5L} as a duration and {@link TimeUnit#SECONDS} as a unit.</p>
     */
    protected CachedMessageBundle()
    {
        this(5L, TimeUnit.SECONDS);
    }

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
                 * If not, or it exists but has been cancelled (which happens on
                 * a timeout), create the and run it asynchronously.
                 */
                task = lookupTask(locale);
                lookups.put(locale, task);
                service.execute(task);
            }
        }

        /*
         * Try and get the result for this locale; on any failure event (either
         * an IOException thrown by tryAndLookup(), a thread interrupt or a
         * timeout), return an empty list.
         */
        try {
            return Arrays.asList(task.get(duration, timeUnit));
        } catch (ExecutionException ignored) {
            return Collections.emptyList();
        } catch (InterruptedException ignored) {
            return Collections.emptyList();
        } catch (TimeoutException ignored) {
            // Cancel the task if it has timed out
            task.cancel(true);
            return Collections.emptyList();
        }
    }

    /**
     * Try and look up the message source for a given locale
     *
     * <p>It is guaranteed that the {@code locale} argument is never null; this
     * means the "no locale" call is really a call with {@link Locale#ROOT} as
     * an argument.</p>
     *
     * <p>This method MUST NOT return {@code null}.</p>
     *
     * @param locale the locale to look up
     * @return the matching {@link MessageSource}
     * @throws IOException failed
     */
    protected abstract MessageSource tryAndLookup(final Locale locale)
        throws IOException;

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
