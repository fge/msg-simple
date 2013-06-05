package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.provider.load.MessageSourceLoader;
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

public final class LoadingMessageSourceProvider
    implements MessageSourceProvider
{
    private static final int NTHREADS = 5;

    private final MessageSourceLoader loader;
    private final MessageSource defaultSource;
    private final ExecutorService service
        = Executors.newFixedThreadPool(NTHREADS);
    private final Map<Locale, FutureTask<MessageSource>> sources
        = new HashMap<Locale, FutureTask<MessageSource>>();

    private LoadingMessageSourceProvider(final Builder builder)
    {
        loader = builder.loader;
        defaultSource = builder.defaultSource;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    @Override
    public MessageSource getMessageSource(final Locale locale)
    {
        FutureTask<MessageSource> task;

        synchronized (sources) {
            task = sources.get(locale);
            if (task == null) {
                task = loadingTask(locale);
                sources.put(locale, task);
                service.execute(task);
            }
        }

        try {
            final MessageSource source;
            source = task.get();
            return source == null ? defaultSource : source;
        } catch (InterruptedException ignored) {
            return defaultSource;
        } catch (ExecutionException ignored) {
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

        public MessageSourceProvider build()
        {
            if (loader == null)
                throw new IllegalArgumentException("no loader has been provided");
            return new LoadingMessageSourceProvider(this);
        }
    }
}
