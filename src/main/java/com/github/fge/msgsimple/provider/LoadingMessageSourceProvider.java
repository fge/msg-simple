package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.provider.load.MessageSourceLoader;
import com.github.fge.msgsimple.source.MessageSource;

import java.io.IOException;
import java.util.Locale;

public final class LoadingMessageSourceProvider
    implements MessageSourceProvider
{
    private final MessageSourceLoader loader;
    private final MessageSource defaultSource;

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
        try {
            final MessageSource source = loader.load(locale);
            return source == null ? defaultSource : source;
        } catch (IOException ignored) {
            return defaultSource;
        }
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
