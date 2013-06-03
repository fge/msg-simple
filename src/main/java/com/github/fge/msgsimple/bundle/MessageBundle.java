package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Base abstract class for message bundles
 *
 * <p>When looking up a message for a given key and locale (using either of
 * {@link #getKey(String, Locale)} or {@link #getKey(String, String)}), the
 * locales are queried, from the more specific to the more general, for a list
 * of {@link MessageSource}s. The first source having a matching key wins.</p>
 *
 * <p>This is essentially the same as what the JDK's {@link ResourceBundle}
 * does, but with one difference: if the key is not found in any source,
 * the key itself is returned, instead of throwin an (unchecked!) exception.</p>
 *
 * @see LocaleUtils#getApplicable(Locale)
 * @see FixedMessageBundle
 * @see CachedMessageBundle
 * @see PropertiesMessageBundle
 */
@ThreadSafe
public abstract class MessageBundle
{
    /**
     * Get a message for the given key and locale
     *
     * @param key the key
     * @param locale the locale
     * @return a matching message if found; the key itself if no message is
     * found
     * @throws NullPointerException either the key or the locale is null
     */
    public final String getKey(final String key, final Locale locale)
    {
        if (key == null)
            throw new NullPointerException("null keys are not allowed");
        if (locale == null)
            throw new NullPointerException("null locales are not allowed");

        String ret;

        for (final Locale candidate: LocaleUtils.getApplicable(locale))
            for (final MessageSource source: getSources(candidate)) {
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
     * #getKey(String, Locale)}.</p>
     *
     * @param key the key to look up
     * @param locale the locale
     * @return a matching message if found; the key itself if no message is
     * found
     * @throws NullPointerException either the key or the locale is null
     * @throws IllegalArgumentException cannot parse locale string
     * @see LocaleUtils#parseLocale(String)
     */
    public final String getKey(final String key, final String locale)
    {
        return getKey(key, LocaleUtils.parseLocale(locale));
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
    public final String getKey(final String key)
    {
        return getKey(key, Locale.getDefault());
    }

    /**
     * Get the list of message sources for a given locale
     *
     * <p>This method must NEVER return {@code null}. If no sources are
     * applicable, an empty list should be returned.</p>
     *
     * @param locale the locale
     * @return the list of message sources for this locale; an empty list if no
     * sources are applicable
     */
    protected abstract List<MessageSource> getSources(final Locale locale);
}
