package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Immutable
public final class StaticMessageSourceProvider
    implements MessageSourceProvider
{
    private final MessageSource defaultSource;
    private final Map<Locale, MessageSource> sources;

    public static Builder newBuilder()
    {
        return new Builder();
    }

    private StaticMessageSourceProvider(final Builder builder)
    {
        defaultSource = builder.defaultSource;
        sources = new HashMap<Locale, MessageSource>(builder.sources);
    }

    @Override
    public MessageSource getMessageSource(final Locale locale)
    {
        return sources.containsKey(locale) ? sources.get(locale)
            : defaultSource;
    }

    @NotThreadSafe
    public static final class Builder
    {
        private MessageSource defaultSource;
        private final Map<Locale, MessageSource> sources
            = new HashMap<Locale, MessageSource>();

        Builder()
        {
        }

        public Builder addSource(final Locale locale,
            final MessageSource source)
        {
            if (locale == null)
                throw new NullPointerException("null keys are not allowed");
            if (source == null)
                throw new NullPointerException("null sources are not allowed");
            sources.put(locale, source);
            return this;
        }

        public Builder setDefaultSource(final MessageSource source)
        {
            if (source == null)
                throw new NullPointerException("cannot set null default source");
            defaultSource = source;
            return this;
        }

        public StaticMessageSourceProvider build()
        {
            return new StaticMessageSourceProvider(this);
        }
    }
}
