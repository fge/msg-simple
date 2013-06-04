package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Locale;

@Immutable
public final class StaticMessageSourceProvider
    implements MessageSourceProvider
{
    private final MessageSource defaultSource;

    public static Builder newBuilder()
    {
        return new Builder();
    }

    private StaticMessageSourceProvider(final Builder builder)
    {
        defaultSource = builder.defaultSource;
    }

    @Override
    public MessageSource getMessageSource(final Locale locale)
    {
        return defaultSource;
    }

    @NotThreadSafe
    public static final class Builder
        implements com.github.fge.Builder<StaticMessageSourceProvider>
    {
        private MessageSource defaultSource;

        Builder()
        {
        }

        public Builder setDefaultSource(final MessageSource source)
        {
            defaultSource = source;
            return this;
        }

        @Override
        public StaticMessageSourceProvider build()
        {
            return new StaticMessageSourceProvider(this);
        }
    }
}
