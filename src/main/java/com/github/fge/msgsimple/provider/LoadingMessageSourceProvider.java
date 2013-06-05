package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.HashMap;
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
 * A caching, on-demand loading message source provider
 *
 * <p>This class uses a {@link MessageSourceLoader} internally to look up
 * message sources. As is the case for {@link StaticMessageSourceProvider}, you
 * can also set a default source if the loader fails to grab a source.</p>
 *
 * <p>Important notes:</p>
 *
 * <ul>
 *     <li>the default source is also returned if the load fails with an
 *     exception;</li>
 *     <li>when a source is loaded, it is permanently cached;</li>
 *     <li>there is also a timeout for loading (which is 5 seconds by default);
 *     if the timeout is reached, the loading is attempted again the next time
 *     the locale is asked for.</li>
 * </ul>
 *
 * <p>You cannot instantiate that class directly; use {@link #newBuilder()} to
 * obtain a builder class and set up your provider.</p>
 */
@ThreadSafe
public final class LoadingMessageSourceProvider
    implements MessageSourceProvider
{
    private static final int NTHREADS = 5;

    private final MessageSourceLoader loader;
    private final MessageSource defaultSource;
    private final long nr;
    private final TimeUnit unit;
    private final ExecutorService service
        = Executors.newFixedThreadPool(NTHREADS);
    private final Map<Locale, FutureTask<MessageSource>> sources
        = new HashMap<Locale, FutureTask<MessageSource>>();

    private LoadingMessageSourceProvider(final Builder builder)
    {
        loader = builder.loader;
        defaultSource = builder.defaultSource;
        nr = builder.nr;
        unit = builder.unit;
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
        FutureTask<MessageSource> task;

        /*
         * The algorithm is as follows:
         *
         * - access the sources map in a synchronous manner;
         * - grab the FutureTask matching the required locale:
         *     - if no task exists, create it;
         *     - if it exists but has been cancelled (in the event of a timeout,
         *       see below), create it anew;
         * - always within the synchronized access to sources, submit the task
         *   for immediate execution to our ExecutorService;
         * - to be followed...
         */
        synchronized (sources) {
            task = sources.get(locale);
            if (task == null || task.isCancelled()) {
                task = loadingTask(locale);
                sources.put(locale, task);
                service.execute(task);
            }
        }

        /*
         * - try and get the result of the task, with a timeout;
         * - if we get a result in time, return it, or the default source if
         *   the result is null;
         * - in the event of an error, return the default source; in addition,
         *   if this is a timeout, cancel the task.
         */
        try {
            final MessageSource source = task.get(nr, unit);
            return source == null ? defaultSource : source;
        } catch (InterruptedException ignored) {
            return defaultSource;
        } catch (ExecutionException ignored) {
            return defaultSource;
        } catch (TimeoutException ignored) {
            task.cancel(true);
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

    /**
     * Builder class for a {@link LoadingMessageSourceProvider}
     */
    public static final class Builder
    {
        private MessageSourceLoader loader;
        private MessageSource defaultSource;
        private long nr = 5L;
        private TimeUnit unit = TimeUnit.SECONDS;

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
            if (loader == null)
                throw new NullPointerException("loader cannot be null");
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
            if (defaultSource == null)
                throw new NullPointerException("default source cannot be null");
            this.defaultSource = defaultSource;
            return this;
        }

        /**
         * Set the load timeout
         *
         * @param nr number of units
         * @param unit the time unit
         * @throws IllegalArgumentException {@code nr} is negative or zero
         * @throws NullPointerException time unit is null
         * @return this
         */
        public Builder setTimeout(final long nr, final TimeUnit unit)
        {
            if (nr <= 0L)
                throw new IllegalArgumentException("timeout must be greater " +
                    "than 0");
            if (unit == null)
                throw new NullPointerException("time unit must not be null");
            this.nr = nr;
            this.unit = unit;
            return this;
        }

        /**
         * Build the provider
         *
         * @return a {@link LoadingMessageSourceProvider}
         */
        public MessageSourceProvider build()
        {
            if (loader == null)
                throw new IllegalArgumentException("no loader has been provided");
            return new LoadingMessageSourceProvider(this);
        }
    }
}
