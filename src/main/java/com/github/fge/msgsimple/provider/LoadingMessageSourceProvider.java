/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.InternalBundle;
import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A caching, on-demand loading message source provider with configurable expiry
 *
 * <p>This class uses a {@link MessageSourceLoader} internally to look up
 * message sources. As is the case for {@link StaticMessageSourceProvider}, you
 * can also set a default source if the loader fails to grab a source.</p>
 *
 * <p>Apart from the loader, you can customize two aspects of the provider:</p>
 *
 * <ul>
 *     <li>its load timeout (1 second by default);</li>
 *     <li>its expiry time (10 minutes by default).</li>
 * </ul>
 *
 * <p>Note that the expiry time is periodic only, and not per source. The
 * loading result (success or failure) is recorded permanently until the expiry
 * time kicks in.</p>
 *
 * <p>In the event of a timeout, the task remains active until it gets a result;
 * this means, for instance, that if you set up a timeout of 500 milliseconds,
 * but the task takes 2 seconds to complete, during these two seconds, the
 * default source will be returned instead.</p>
 *
 * <p>You can also configure a loader so that it never expires.</p>
 *
 * <p>You cannot instantiate that class directly; use {@link #newBuilder()} to
 * obtain a builder class and set up your provider.</p>
 *
 * @see Builder
 */
@ThreadSafe
public final class LoadingMessageSourceProvider
    implements MessageSourceProvider
{
    /*
     * Use daemon threads. We don't give control to the user about the
     * ExecutorService, and we don't have a reliable way to shut it down (a JVM
     * shutdown hook does not get involved on a webapp shutdown, so we cannot
     * use that...).
     */
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory()
    {
        private final ThreadFactory factory = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(final Runnable r)
        {
            final Thread ret = factory.newThread(r);
            ret.setDaemon(true);
            return ret;
        }
    };

    private static final InternalBundle BUNDLE = InternalBundle.getInstance();

    private static final int NTHREADS = 3;

    /*
     * Executor service for loading tasks
     */
    private final ExecutorService service
        = Executors.newFixedThreadPool(NTHREADS, THREAD_FACTORY);

    /*
     * Loader and default source
     */
    private final MessageSourceLoader loader;
    private final MessageSource defaultSource;

    /*
     * Timeout
     */
    private final long timeoutDuration;
    private final TimeUnit timeoutUnit;

    /*
     * Expiry
     *
     * Note that expiry is set up, if necessary, in the first call to
     * .getMessage().
     */
    private final AtomicBoolean expiryEnabled;
    private final long expiryDuration;
    private final TimeUnit expiryUnit;

    /*
     * List of sources
     */
    private final Map<Locale, FutureTask<MessageSource>> sources
        = new HashMap<Locale, FutureTask<MessageSource>>();

    private LoadingMessageSourceProvider(final Builder builder)
    {
        loader = builder.loader;
        defaultSource = builder.defaultSource;

        timeoutDuration = builder.timeoutDuration;
        timeoutUnit = builder.timeoutUnit;

        expiryDuration =  builder.expiryDuration;
        expiryUnit = builder.expiryUnit;
        /*
         * Mimic an already enabled expiry if, in fact, there is none
         */
        expiryEnabled = new AtomicBoolean(expiryDuration == 0L);
    }

    /**
     * Create a new builder
     *
     * @return an empty builder
     */
    public static Builder newBuilder()
    {
        return new Builder();
    }

    @Override
    public MessageSource getMessageSource(final Locale locale)
    {
        /*
         * Set up expiry, if necessary
         */
        if (!expiryEnabled.getAndSet(true))
            setupExpiry(expiryDuration, expiryUnit);

        FutureTask<MessageSource> task;

        /*
         * The algorithm is as follows:
         *
         * - access the sources map in a synchronous manner (the expiry task
         *   also does this);
         * - grab the FutureTask matching the required locale:
         *     - if no task exists, create it;
         *     - if it exists but has been cancelled (in the event of a timeout,
         *       see below), create a new task;
         * - always within the synchronized access to sources, submit the task
         *   for immediate execution to our ExecutorService;
         * - to be followed...
         */
        synchronized (sources) {
            task = sources.get(locale);
            if (task == null) {
                task = loadingTask(locale);
                sources.put(locale, task);
                service.execute(task);
            }
        }

        /*
         * - try and get the result of the task, with a timeout;
         * - if we get a result in time, return it, or the default source (if
         *   any) if the result is null;
         * - in the event of an error, return the default source; in addition,
         *   if this is a timeout, cancel the task.
         */
        try {
            final MessageSource source = task.get(timeoutDuration, timeoutUnit);
            return source == null ? defaultSource : source;
        } catch (InterruptedException ignored) {
            /*
             * Restore interrupt state. We will not throw the exception here,
             * since static providers exist, which do not throw it -- and how
             * would a message provider API sound to you if you had to catch
             * InterruptedException each time you try and fetch a message?
             *
             * Let the caller deal with that.
             */
            Thread.currentThread().interrupt();
            return defaultSource;
        } catch (ExecutionException ignored) {
            return defaultSource;
        } catch (TimeoutException ignored) {
            /*
             * Cancel the task here; other potential users of this locale will
             * be greeted with a CancellationException, until one enters this
             * method and resets the task.
             */
            return defaultSource;
        } catch (CancellationException ignored) {
            /*
             * Ugly :( Unfortunately this can happen. Scenario:
             *
             * thread1            thread2
             * --------           --------
             * grabs task
             *                    cleanup: cancel task
             * get()s
             * BOOM: CancellationException
             */
            return defaultSource;
        }
    }

    private FutureTask<MessageSource> loadingTask(final Locale locale)
    {
        return new FutureTask<MessageSource>(new Callable<MessageSource>()
        {
            @Override
            public MessageSource call()
                throws IOException
            {
                return loader.load(locale);
            }
        });
    }

    private void setupExpiry(final long duration, final TimeUnit unit)
    {
        final Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                final List<FutureTask<MessageSource>> tasks;
                synchronized (sources) {
                    tasks = new ArrayList<FutureTask<MessageSource>>(
                        sources.values());
                    sources.clear();
                }
                for (final FutureTask<MessageSource> task: tasks)
                    task.cancel(true);
            }
        };
        // Overkill?
        final ScheduledExecutorService scheduled
            = Executors.newScheduledThreadPool(1, THREAD_FACTORY);
        scheduled.scheduleAtFixedRate(runnable, duration, duration, unit);
    }

    /**
     * Builder class for a {@link LoadingMessageSourceProvider}
     */
    public static final class Builder
    {
        /*
         * NOTE: apart from requiring them to be positive, we do no checks at
         * all on what the user submits as timeout/expiry values; it could
         * want a 1 ns expiry that we woudln't prevent it.
         */
        private MessageSourceLoader loader;
        private MessageSource defaultSource;
        private long timeoutDuration = 1L;
        private TimeUnit timeoutUnit = TimeUnit.SECONDS;
        private long expiryDuration = 10L;
        private TimeUnit expiryUnit = TimeUnit.MINUTES;

        private Builder()
        {
        }

        /**
         * Set the message source loader
         *
         * @param loader the loader
         * @throws NullPointerException loader is null
         * @return this
         */
        public Builder setLoader(final MessageSourceLoader loader)
        {
            BUNDLE.checkNotNull(loader, "cfg.nullLoader");
            this.loader = loader;
            return this;
        }

        /**
         * Set the default message source if the loader fails to load
         *
         * @param defaultSource the default source
         * @throws NullPointerException source is null
         * @return this
         */
        public Builder setDefaultSource(final MessageSource defaultSource)
        {
            BUNDLE.checkNotNull(defaultSource, "cfg.nullDefaultSource");
            this.defaultSource = defaultSource;
            return this;
        }

        /**
         * Set the load timeout (1 second by default)
         *
         * <p>If the loader passed as an argument fails to load a message
         * source after the specified timeout is elapsed, then the default
         * messagesource will be returned (if any).</p>
         *
         * @param duration number of units
         * @param unit the time unit
         * @throws IllegalArgumentException {@code duration} is negative or zero
         * @throws NullPointerException {@code unit} is null
         * @return this
         *
         * @see #setLoader(MessageSourceLoader)
         * @see #setDefaultSource(MessageSource)
         */
        public Builder setLoadTimeout(final long duration, final TimeUnit unit)
        {
            BUNDLE.checkArgument(duration > 0L, "cfg.nonPositiveDuration");
            BUNDLE.checkNotNull(unit, "cfg.nullTimeUnit");
            timeoutDuration = duration;
            timeoutUnit = unit;
            return this;
        }

        /**
         * Set the source expiry time (10 minutes by default)
         *
         * <p>Do <b>not</b> use this method if you want no expiry at all; use
         * {@link #neverExpires()} instead.</p>
         *
         * @since 0.5
         *
         * @param duration number of units
         * @param unit the time unit
         * @throws IllegalArgumentException {@code duration} is negative or zero
         * @throws NullPointerException {@code unit} is null
         * @return this
         */
        public Builder setExpiryTime(final long duration, final TimeUnit unit)
        {
            BUNDLE.checkArgument(duration > 0L, "cfg.nonPositiveDuration");
            BUNDLE.checkNotNull(unit, "cfg.nullTimeUnit");
            expiryDuration = duration;
            expiryUnit = unit;
            return this;
        }

        /**
         * Set this loading provider so that entries never expire
         *
         * @since 0.5
         *
         * @return this
         */
        public Builder neverExpires()
        {
            expiryDuration = 0L;
            return this;
        }

        /**
         * Build the provider
         *
         * @return a {@link LoadingMessageSourceProvider}
         * @throws IllegalArgumentException no loader has been provided
         */
        public MessageSourceProvider build()
        {
            BUNDLE.checkArgument(loader != null, "cfg.noLoader");
            return new LoadingMessageSourceProvider(this);
        }
    }
}
