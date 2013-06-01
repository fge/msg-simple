package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.source.MessageSource;

import java.util.Locale;

public abstract class I18NMessageBundle
{
    protected abstract Iterable<MessageSource> getSources(final Locale locale);

    public final String getKey(final String key, final Locale locale)
    {
        if (key == null)
            throw new NullPointerException("null keys are not allowed");
        if (locale == null)
            throw new NullPointerException("null locales are not allowed");

        Iterable<MessageSource> sources;
        String ret;

        for (final Locale candidate: LocaleUtils.getApplicable(locale)) {
            sources = getSources(candidate);
            for (final MessageSource source: sources) {
                ret = source.getKey(key);
                if (ret != null)
                    return ret;
            }
        }

        // No source found at all... Should not happen!
        throw new IllegalStateException("no message source found! " +
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
