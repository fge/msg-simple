package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Static message source provider
 *
 * <p>The simplest form of such a provider is one which only has a default
 * message source. You can define specific message sources for specific
 * locales too.</p>
 *
 * <p>You cannot instantiate this class directly: you must use a {@link Builder}
 * (using the {@link #newBuilder()} method).</p>
 *
 * @see Builder
 */
@Immutable
public final class StaticMessageSourceProvider
    implements MessageSourceProvider
{
    private final MessageSource defaultSource;
    private final Map<Locale, MessageSource> sources;

    /**
     * Create a new static source builder
     *
     * @return a builder
     */
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

    /**
     * Builder for a {@link StaticMessageSourceProvider}
     */
    @NotThreadSafe
    public static final class Builder
    {
        private MessageSource defaultSource;
        private final Map<Locale, MessageSource> sources
            = new HashMap<Locale, MessageSource>();

        Builder()
        {
        }

        /**
         * Add a message source for a given locale
         *
         * @param locale the locale
         * @param source the message source
         * @throws NullPointerException either the locale or the source is null
         * @return this
         */
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

        /**
         * Set a default message source
         *
         * @param source the message source
         * @throws NullPointerException source is null
         * @return this
         */
        public Builder setDefaultSource(final MessageSource source)
        {
            if (source == null)
                throw new NullPointerException("cannot set null default source");
            defaultSource = source;
            return this;
        }

        /**
         * Build the message source provider
         *
         * @return a {@link StaticMessageSourceProvider}
         */
        public MessageSourceProvider build()
        {
            return new StaticMessageSourceProvider(this);
        }
    }
}
