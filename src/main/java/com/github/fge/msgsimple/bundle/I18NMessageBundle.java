package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Locale;

/**
 * Base abstract class for locale-enabled message bundles
 *
 * <p>You will want to use this class for all locale-enabled message lookups.
 * </p>
 *
 * <p>The basic principle of this bundle is to associate a list of {@link
 * MessageSource}s to locales (including {@link Locale#ROOT}).</p>
 *
 * <p>When looking up a message for a given key and locale (using either of
 * {@link #getKey(String, Locale)} or {@link #getKey(String, String)}), the
 * locales are queried, from the more specific to the more general, for a list
 * of {@link MessageSource}s (similarly to what {@link MessageBundle} does). The
 * first source having a matching key wins. Also, similarly to what {@link
 * MessageBundle#getKey(String)} does, if no message is found for the key, the
 * key itself is returned.</p>
 *
 * <p>This class also has static factory methods to build various instances of
 * its existing implementations.</p>
 *
 * @see LocaleUtils#getApplicable(Locale)
 * @see StaticI18NBundle
 * @see CachedI18NMessageBundle
 * @see UTF8ResouceBundle
 * @see Builder
 */
@ThreadSafe
public abstract class I18NMessageBundle
{
    /**
     * Return a new builder for a {@link StaticI18NBundle}
     *
     * @return a {@link Builder}
     */
    public static Builder newStaticBundle()
    {
        return new StaticI18NBundle.Builder();
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

    /**
     * Create a mutable version of this bundle, if possible
     *
     * @return a {@link Builder} with this bundle's data
     * @throws IllegalStateException cannot create a builder for this bundle
     */
    public abstract Builder modify();

    /**
     * Abstract builder class for an {@link I18NMessageBundle}
     */
    @NotThreadSafe
    public abstract static class Builder
    {
        public final Builder appendSource(final Locale locale,
            final MessageSource source)
        {
            if (locale == null)
                throw new NullPointerException("locale is null");
            if (source == null)
                throw new NullPointerException("message source is null");
            doAppendSource(locale, source);
            return this;
        }

        public final Builder prependSource(final Locale locale,
            final MessageSource source)
        {
            if (locale == null)
                throw new NullPointerException("locale is null");
            if (source == null)
                throw new NullPointerException("message source is null");
            doPrependSource(locale, source);
            return this;
        }

        protected abstract void doAppendSource(final Locale locale,
            final MessageSource source);

        protected abstract void doPrependSource(final Locale locale,
            final MessageSource source);

        protected abstract I18NMessageBundle build();
    }
}
