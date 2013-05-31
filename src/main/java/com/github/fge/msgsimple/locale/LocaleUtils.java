package com.github.fge.msgsimple.locale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class LocaleUtils
{
    private LocaleUtils()
    {
    }

    private static Collection<Locale> getApplicable(final Locale target)
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
