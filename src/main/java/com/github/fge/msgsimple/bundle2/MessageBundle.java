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

package com.github.fge.msgsimple.bundle2;

import com.github.fge.Frozen;
import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Base abstract class for message bundles
 *
 * <p>When looking up a message for a given key and locale (using either of
 * {@link #getMessage(String, Locale)} or {@link #getMessage(String, String)}),
 * the locales are queried, from the more specific to the more general, for a
 * list of {@link MessageSourceProvider}s. If the provider has a {@link
 * MessageSource} for this locale, it is queried for the key. The first message
 * source having an entry for that key wins.</p>
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

    public static MessageBundleBuilder newBuilder()
    {
        return new MessageBundleBuilder();
    }

    MessageBundle(final MessageBundleBuilder builder)
    {
        providers.addAll(builder.providers);
    }

    /**
     * Get a message for the given key and locale
     *
     * @param key the key
     * @param locale the locale
     * @return a matching message if found; the key itself if no message is
     * found
     * @throws NullPointerException either the key or the locale is null
     */
    public String getMessage(final String key, final Locale locale)
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
     * Return a message for a given key and locale
     *
     * <p>This tries and parses the locale, then calls {@link
     * #getMessage(String, Locale)}.</p>
     *
     * @param key the key to look up
     * @param locale the locale
     * @return a matching message if found; the key itself if no message is
     * found
     * @throws NullPointerException either the key or the locale is null
     * @throws IllegalArgumentException cannot parse locale string
     * @see LocaleUtils#parseLocale(String)
     */
    public String getMessage(final String key, final String locale)
    {
        return getMessage(key, LocaleUtils.parseLocale(locale));
    }

    public String getMessage(final String key)
    {
        return getMessage(key, Locale.getDefault());
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
}
