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
 * <p>Convenience static factory methods exist for building a message source
 * provider from a single message source; for more elaborage scenarios, you need
 * to use a {@link Builder} (using the {@link #newBuilder()} method).</p>
 *
 * @see Builder
 */
@Immutable
public final class StaticMessageSourceProvider
    implements MessageSourceProvider
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

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

    /**
     * Convenience method to create a provider with a single source
     *
     * <p>This source will be used for all locales.</p>
     *
     * @param source the message source
     * @return a message provider
     * @see Builder#setDefaultSource(MessageSource)
     */
    public static MessageSourceProvider withSingleSource(
        final MessageSource source)
    {
        return new Builder().setDefaultSource(source).build();
    }

    /**
     * Convenience method to create a provider with a single source for a
     * specific locale
     *
     * @param locale the locale
     * @param source the message source
     * @return a message provider
     * @see Builder#addSource(Locale, MessageSource)
     */
    public static MessageSourceProvider withSingleSource(final Locale locale,
        final MessageSource source)
    {
        return new Builder().addSource(locale, source).build();
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

        private Builder()
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
            BUNDLE.checkNotNull(locale, "cfg.nullKey");
            BUNDLE.checkNotNull(source, "cfg.nullSource");
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
            BUNDLE.checkNotNull(source, "cfg.nullDefaultSource");
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
