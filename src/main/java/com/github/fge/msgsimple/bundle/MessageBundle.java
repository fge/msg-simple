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
import com.github.fge.msgsimple.InternalBundle;
import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.ThreadSafe;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.IllegalFormatException;
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
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

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
        BUNDLE.checkNotNull(key, "query.nullKey");
        BUNDLE.checkNotNull(locale, "query.nullLocale");

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
     * Get a locale-dependent {@code printf()}-like formatted message
     *
     * <p>This is essentially a call to {@link String#format(Locale, String,
     * Object...)}.</p>
     *
     * <p>There is however one important difference: while {@code
     * String.format()} will throw an {@link IllegalFormatException} if an
     * argument is missing or a format specifier is incorrect,  this method
     * returns the format itself (or the key, like {@link #getMessage(Locale,
     * String)}, if no match was found for that locale/key pair).</p>
     *
     * @param locale the locale
     * @param key the key
     * @param params the format parameters
     * @return the formatted message
     * @see Formatter
     */
    public String printf(final Locale locale, final String key,
        final Object... params)
    {
        final String format = getMessage(locale, key);
        try {
            return String.format(locale, format, params);
        } catch (IllegalFormatException ignored) {
            return format;
        }
    }

    /**
     * Get a {@code printf()}-like message with the default locale
     *
     * <p>This calls {@link #printf(Locale, String, Object...)} with {@link
     * Locale#getDefault()} as the first argument.</p>
     *
     * @param key the key
     * @param params the format parameters
     * @return the formatted message
     */
    public String printf(final String key, final Object... params)
    {
        return printf(Locale.getDefault(), key,params);
    }

    public String format(final Locale locale, final String key,
        final Object... params)
    {
        final String pattern = getMessage(locale, key);
        try {
            return new MessageFormat(pattern, locale)
                .format(params, new StringBuffer(pattern.length()), null)
                .toString();
        } catch (IllegalArgumentException ignored) {
            return pattern;
        }
    }

    public String format(final String key, final Object... params)
    {
        return format(Locale.getDefault(), key, params);
    }

    /**
     * Check an object reference for {@code null} and return it
     *
     * <p>If the reference is null, a {@link NullPointerException} is thrown,
     * with its message set according to the given locale and key.</p>
     *
     * @param reference the reference to check
     * @param locale the locale to use
     * @param key the key to use
     * @param <T> type of the reference
     * @return the reference
     * @throws NullPointerException see description
     * @see #getMessage(Locale, String)
     */
    public <T> T checkNotNull(final T reference, final Locale locale,
        final String key)
    {
        if (reference == null)
            throw new NullPointerException(getMessage(locale, key));
        return reference;
    }

    /**
     * Check a reference for {@code null} and return it
     *
     * <p>This calls {@link #checkNotNull(Object, Locale, String)} with the
     * JVM's current locale.</p>
     *
     * @param reference the reference to check
     * @param key the key
     * @param <T> type of the reference
     * @return the reference
     * @throws NullPointerException see description
     */
    public <T> T checkNotNull(final T reference, final String key)
    {
        return checkNotNull(reference, Locale.getDefault(), key);
    }

    /**
     * Check a reference for {@code null} and return it
     *
     * <p>Like {@link #checkNotNull(Object, Locale, String)}, except that this
     * will use {@link #printf(Locale, String, Object...)}</p>
     *
     * @param reference the reference to check
     * @param locale the locale to use
     * @param key the key
     * @param params arguments for the format string
     * @param <T> type of the reference
     * @return the reference
     * @throws NullPointerException see description
     * @see #printf(Locale, String, Object...)
     */
    public <T> T checkNotNullPrintf(final T reference, final Locale locale,
        final String key, final Object... params)
    {
        if (reference == null)
            throw new NullPointerException(printf(locale, key, params));
        return reference;
    }

    /**
     * Check a reference for {@code null} and return it
     *
     * <p>This calls {@link #checkNotNullPrintf(Object, Locale, String,
     * Object...)} with the JVM's current locale.</p>
     *
     * @param reference the reference
     * @param key the key
     * @param params arguments for the format string
     * @param <T> type of the reference
     * @return the reference
     * @throws NullPointerException see description
     */
    public <T> T checkNotNullPrintf(final T reference, final String key,
        final Object... params)
    {
        return checkNotNullPrintf(reference, Locale.getDefault(), key, params);
    }

    /**
     * Check for a condition
     *
     * <p>When the condition is false, an {@link IllegalArgumentException} is
     * thrown with its message set using the given locale and key.</p>
     *
     * @param condition the condition to check
     * @param locale the locale to use
     * @param key the key
     * @throws IllegalArgumentException see description
     * @see #getMessage(Locale, String)
     */
    public void checkArgument(final boolean condition, final Locale locale,
        final String key)
    {
        if (!condition)
            throw new IllegalArgumentException(getMessage(locale, key));
    }

    /**
     * Check for a condition
     *
     * <p>This calls {@link #checkArgument(boolean, Locale, String)} using the
     * JVM's current locale.</p>
     *
     * @param condition the condition to check
     * @param key the key
     * @throws IllegalArgumentException see description
     */
    public void checkArgument(final boolean condition, final String key)
    {
        checkArgument(condition, Locale.getDefault(), key);
    }

    /**
     * Check for a condition
     *
     * <p>Like {@link #checkArgument(boolean, Locale, String)}, except that
     * {@link #printf(Locale, String, Object...)} is used to fill the message.
     * </p>
     *
     * @param condition the condition to check
     * @param locale the locale
     * @param key the key
     * @param params arguments for the format string
     * @throws IllegalArgumentException see description
     * @see #printf(Locale, String, Object...)
     */
    public void checkArgumentPrintf(final boolean condition,
        final Locale locale, final String key, final Object... params)
    {
        if (!condition)
            throw new IllegalArgumentException(printf(locale, key, params));
    }

    /**
     * Check for a condition
     *
     * <p>This calls {@link #checkArgumentPrintf(boolean, Locale, String,
     * Object...)} with the JVM's current locale.</p>
     *
     * @param condition the condition to check
     * @param key the key
     * @param params arguments for the format string
     * @throws IllegalArgumentException see description
     */
    public void checkArgumentPrintf(final boolean condition, final String key,
        final Object... params)
    {
        checkArgumentPrintf(condition, Locale.getDefault(), key, params);
    }

    @Override
    public MessageBundleBuilder thaw()
    {
        return new MessageBundleBuilder(this);
    }
}
