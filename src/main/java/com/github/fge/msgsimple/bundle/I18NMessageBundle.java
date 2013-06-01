package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;

import java.util.Locale;

public abstract class I18NMessageBundle
{
    protected abstract MessageBundle getBundle(final Locale locale);

    public final String getKey(final String key, final Locale locale)
    {
        if (key == null)
            throw new NullPointerException("null keys are not allowed");
        if (locale == null)
            throw new NullPointerException("null locales are not allowed");

        MessageBundle bundle;

        for (final Locale candidate: LocaleUtils.getApplicable(locale)) {
            bundle = getBundle(candidate);
            if (bundle != null)
                return bundle.getKey(key);
        }

        // No bundle found at all... Should not happen!
        throw new IllegalStateException("no bundle found! " +
            "How did we get there at all?");
    }

    public final String getKey(final String key, final String locale)
    {
        return getKey(key, LocaleUtils.parseLocale(locale));
    }

    public final String getKey(final String key)
    {
        return getKey(key, Locale.getDefault());
    }
}
