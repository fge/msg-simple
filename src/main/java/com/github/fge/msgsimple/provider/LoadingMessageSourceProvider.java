package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;

import java.util.Locale;

public final class LoadingMessageSourceProvider
    implements MessageSourceProvider
{
    private LoadingMessageSourceProvider(final Builder builder)
    {
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    @Override
    public MessageSource getMessageSource(final Locale locale)
    {
        return null;
    }

    public static final class Builder
    {
        Builder()
        {
        }

        public LoadingMessageSourceProvider build()
        {
            return new LoadingMessageSourceProvider(this);
        }
    }
}
