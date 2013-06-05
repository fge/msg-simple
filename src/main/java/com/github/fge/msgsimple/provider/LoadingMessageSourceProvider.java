package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;

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

    public static final class Builder
    {
        private MessageSourceLoader loader;
        private MessageSource defaultSource;
        private long nr = 5L;
        private TimeUnit unit = TimeUnit.SECONDS;

        Builder()
        {
        }

        public Builder setLoader(final MessageSourceLoader loader)
        {
            if (loader == null)
                throw new NullPointerException("loader cannot be null");
            this.loader = loader;
            return this;
        }

        public Builder setDefaultSource(final MessageSource defaultSource)
        {
            if (defaultSource == null)
                throw new NullPointerException("default source cannot be null");
            this.defaultSource = defaultSource;
            return this;
        }

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

        public MessageSourceProvider build()
        {
            if (loader == null)
                throw new IllegalArgumentException("no loader has been provided");
            return new LoadingMessageSourceProvider(this);
        }
    }
}
