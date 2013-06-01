package com.github.fge.msgsimple.locale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Utility methods for {@link Locale} management
 *
 * <p>This class provides two methods:</p>
 *
 * <ul>
 *     <li>{@link #parseLocale(String)} parses a string and builds a {@code Locale}
 *     object out of this parsed string (strangely enough, there is no such
 *     method in the JDK!!);</li>
 *     <li>{@link #getApplicable(Locale)} returns an ordered list of locales
 *     "applicable" to the given locale.</li>
 * </ul>
 *
 * <p>The {@link #getApplicable(Locale)} method emulates what the JDK's {@link
 * ResourceBundle} does when you look up a message in a locale; it returns an
 * ordered list from the most specific to the more general. For instance, given
 * the locale {@code "ja_JP_JP"}, it will generate the following list:</p>
 *
 * <ul>
 *     <li>{@code "ja_JP_JP"},</li>
 *     <li>{@code "ja_JP"},</li>
 *     <li>{@code "ja"},</li>
 *     <li>{@code ""} (the root locale, {@link Locale#ROOT}).</li>
 * </ul>
 */
public final class LocaleUtils
{
    private static final Pattern UNDERSCORE = Pattern.compile("_");

    private LocaleUtils()
    {
    }

    /**
     * Parse a string input as an argument and return a locale object
     *
     * <p>Three things to note:</p>
     *
     * <ul>
     *     <li>it is NOT checked whether the extracted language or country codes
     *     are actually registered to the ISO;</li>
     *     <li>all input strings with more than two underscores are deemed
     *     illegal;</li>
     *     <li>if the first component (the language) is empty, {@link
     *     Locale#ROOT} is returned.</li>
     *
     * @see Locale
     *
     * @param input the input string
     * @throws NullPointerException input is null
     * @throws IllegalArgumentException input is malformed (see above)
     * @return a {@link Locale}
     */
    public static Locale parseLocale(final String input)
    {
        if (input == null)
            throw new NullPointerException("input cannot be null");

        if (input.isEmpty())
            return Locale.ROOT;

        final String[] elements = UNDERSCORE.split(input);

        final int len = elements.length;

        if (len == 0)
            return Locale.ROOT;
        if (elements[0].isEmpty())
            return Locale.ROOT;

        switch (elements.length) {
            case 1:
                return new Locale(elements[0]);
            case 2:
                return new Locale(elements[0], elements[1]);
            case 3:
                return new Locale(elements[0], elements[1], elements[2]);
            default:
                throw new IllegalArgumentException("malformed input " + input);
        }
    }

    /**
     * Get a "decrementing" list of candidate locales for a given locale
     *
     * <p>The order of locale returned is from the more specific to the less
     * specific (the latter being {@link Locale#ROOT}).</p>
     *
     * @param target the locale
     * @return the list of applicable locales
     */
    public static Collection<Locale> getApplicable(final Locale target)
    {
        final String language = target.getLanguage();
        final String country = target.getCountry();
        final String variant = target.getVariant();

        final List<Locale> ret = new ArrayList<Locale>();
        ret.add(target);

        Locale locale;

        if (!variant.isEmpty()) {
            locale = new Locale(language, country);
            if (!locale.equals(Locale.ROOT))
                ret.add(locale);
        }

        if (!country.isEmpty()) {
            locale = new Locale(language);
            if (!locale.equals(Locale.ROOT))
                ret.add(locale);
        }

        if (!language.isEmpty())
            ret.add(Locale.ROOT);

        return ret;
    }
}
