package com.github.fge.msgsimple.locale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class LocaleUtils
{
    private static final Pattern UNDERSCORE = Pattern.compile("_");
    private LocaleUtils()
    {
    }

    public static Locale parse(final String input)
    {
        if (input == null)
            throw new NullPointerException("input cannot be null");

        if (input.isEmpty())
            return Locale.ROOT;

        final String[] elements = UNDERSCORE.split(input);

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
