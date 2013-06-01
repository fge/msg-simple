package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.source.MessageSource;

import java.util.List;
import java.util.Locale;

public abstract class I18NMessageBundle
{
    protected abstract List<MessageSource> getSources(final Locale locale);

    public static StaticI18NBundle.Builder newStaticBundle()
    {
        return new StaticI18NBundle.Builder();
    }

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

        // No source found which has the key... Return the key itself.
        return key;
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
