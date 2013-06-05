/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.msgsimple.bundle;

import com.github.fge.Frozen;
import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.provider.StaticMessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Base abstract class for message bundles
 *
 * <p>A bundle is a list of {@link MessageSourceProvider}s. When a message is
 * looked up, providers are queried in order for a {@link MessageSource} for
 * that locale; if a source is found, it is queried for the message.</p>
 *
 * <p>When a method is used which does not take a {@link Locale} as an argument,
 * the default locale is used (obtained via {@link Locale#getDefault()}). If no
 * match is found for the locale, the next, more general locale is tried.</p>
 *
 * <p>Finally, if no match was found for any provider/source, the key itself is
 * returned.</p>
 *
 * <p>You cannot instantiate this class directly: use {@link #newBuilder()} to
 * obtain a builder, then that builder's {@link MessageBundleBuilder#freeze()}
 * method to obtain the bundle; alternatively, you can reuse an existing bundle
 * and {@link #thaw()} it, modify it and freeze it again.</p>
 *
 * @see LocaleUtils#getApplicable(Locale)
 * @see MessageSourceProvider
 */
@ThreadSafe
public final class MessageBundle
    implements Frozen<MessageBundleBuilder>
{
    final List<MessageSourceProvider> providers
        = new ArrayList<MessageSourceProvider>();

    /**
     * Create a new, empty builder for a bundle
     *
     * @return a builder
     */
    public static MessageBundleBuilder newBuilder()
    {
        return new MessageBundleBuilder();
    }

    MessageBundle(final MessageBundleBuilder builder)
    {
        providers.addAll(builder.providers);
    }

    @Deprecated
    private MessageBundle(final Builder builder)
    {
        providers.addAll(builder.providers);
    }

    /**
     * Get a message for the given key and locale
     *
     * @param locale the locale
     * @param key the key
     * @return a matching message if found; the key itself if no message is
     * found
     * @throws NullPointerException either the key or the locale is null
     */
    public String getMessage(final Locale locale, final String key)
    {
        if (key == null)
            throw new NullPointerException("null keys are not allowed");
        if (locale == null)
            throw new NullPointerException("null locales are not allowed");

        String ret;
        MessageSource source;

        for (final Locale l: LocaleUtils.getApplicable(locale))
            for (final MessageSourceProvider provider: providers) {
                source = provider.getMessageSource(l);
                if (source == null)
                    continue;
                ret = source.getKey(key);
                if (ret != null)
                    return ret;
            }

        // No source found which has the key... Return the key itself.
        return key;
    }

    /**
     * Return a message for a given key, using the JVM's current locale
     *
     * @param key the key
     * @return a matching message if found; the key itself if no message is
     * found
     * @throws NullPointerException key is null
     * @see Locale#getDefault()
     * @see Locale#setDefault(Locale)
     */
    public String getMessage(final String key)
    {
        return getMessage(Locale.getDefault(), key);
    }

    /**
     * Return a message for a given key, using the JVM's current locale
     *
     * @param key the key
     * @return a matching message if found; the key itself if no message is
     * found
     * @throws NullPointerException key is null
     * @see Locale#getDefault()
     * @see Locale#setDefault(Locale)
     * @deprecated use {@link #getMessage(String)} instead; will be removed in
     * 0.3
     */
    @Deprecated
    public String getKey(final String key)
    {
        return getMessage(key);
    }

    @Override
    public MessageBundleBuilder thaw()
    {
        return new MessageBundleBuilder(this);
    }

    /**
     * @deprecated use the new builder class instead; will be removed in 0.3
     *
     * @see #newBuilder()
     */
    @Deprecated
    public Builder copy()
    {
        return new Builder(this);
    }

    /**
     * Old builder class
     *
     * @deprecated use the new builder class instead
     * @see MessageBundle#newBuilder()
     */
    @Deprecated
    public static final class Builder
    {
        final List<MessageSourceProvider> providers
            = new ArrayList<MessageSourceProvider>();

        public Builder()
        {
        }

        private Builder(final MessageBundle bundle)
        {
            providers.addAll(bundle.providers);
        }

        /**
         * Append a message provider
         *
         * @param provider the provider
         * @throws NullPointerException provider is null
         * @return this
         */
        public Builder appendProvider(
            final MessageSourceProvider provider)
        {
            if (provider == null)
                throw new NullPointerException("cannot append null provider");
            providers.add(provider);
            return this;
        }

        /**
         * Prepend a message provider
         *
         * @param provider the provider
         * @throws NullPointerException provider is null
         * @return this
         */
        public Builder prependProvider(
            final MessageSourceProvider provider)
        {
            if (provider == null)
                throw new NullPointerException("cannot prepend null provider");
            providers.add(0, provider);
            return this;
        }

        /**
         * Convenience method to append a single-source provider
         *
         * @param source the message source
         * @return this
         * @see StaticMessageSourceProvider#withSingleSource(MessageSource)
         */
        public Builder appendSource(final MessageSource source)
        {
            final MessageSourceProvider provider
                = StaticMessageSourceProvider.withSingleSource(source);
            providers.add(provider);
            return this;
        }

        /**
         * Convenience method to prepend a single-source provider
         *
         * @param source the message source
         * @return this
         * @see StaticMessageSourceProvider#withSingleSource(MessageSource)
         */
        public Builder prependSource(final MessageSource source)
        {
            final MessageSourceProvider provider
                = StaticMessageSourceProvider.withSingleSource(source);
            providers.add(0, provider);
            return this;
        }

        /**
         * Convenience method to append a single-source provider for a given locale
         *
         * @param source the message source
         * @return this
         * @see StaticMessageSourceProvider#withSingleSource(Locale, MessageSource)
         */
        public Builder appendSource(final Locale locale,
            final MessageSource source)
        {
            final MessageSourceProvider provider
                = StaticMessageSourceProvider.withSingleSource(locale, source);
            providers.add(provider);
            return this;
        }

        /**
         * Convenience method to prepend a single-source provider for a given locale
         *
         * @param source the message source
         * @return this
         * @see StaticMessageSourceProvider#withSingleSource(Locale, MessageSource)
         */
        public Builder prependSource(final Locale locale,
            final MessageSource source)
        {
            final MessageSourceProvider provider
                = StaticMessageSourceProvider.withSingleSource(locale, source);
            providers.add(0, provider);
            return this;
        }

        public MessageBundle build()
        {
            return new MessageBundle(this);
        }

    }
}
