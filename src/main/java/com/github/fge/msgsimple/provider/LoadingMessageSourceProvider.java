package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.provider.load.MessageSourceLoader;
import com.github.fge.msgsimple.source.MessageSource;

import java.io.IOException;
import java.util.Locale;

public final class LoadingMessageSourceProvider
    implements MessageSourceProvider
{
    private final MessageSourceLoader loader;

    private LoadingMessageSourceProvider(final Builder builder)
    {
        loader = builder.loader;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    @Override
    public MessageSource getMessageSource(final Locale locale)
    {
        try {
            return loader.load(locale);
        } catch (IOException ignored) {
            return null;
        }
    }

    public static final class Builder
    {
        private MessageSourceLoader loader;

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

        public Builder setDefaultSource(final MessageSource source)
        {
            if (source == null)
                throw new NullPointerException("default source cannot be null");
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
