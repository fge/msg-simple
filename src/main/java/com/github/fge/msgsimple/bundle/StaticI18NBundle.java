package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class StaticI18NBundle
    extends I18NMessageBundle
{
    private final Map<Locale, List<MessageSource>> sources
        = new HashMap<Locale, List<MessageSource>>();

    private StaticI18NBundle(final Builder builder)
    {
        for (final Map.Entry<Locale, List<MessageSource>> entry:
            builder.sources.entrySet())
            sources.put(entry.getKey(),
                new ArrayList<MessageSource>(entry.getValue()));
    }

    @Override
    protected List<MessageSource> getSources(final Locale locale)
    {
        return sources.get(locale);
    }

    @Override
    public I18NMessageBundle.Builder modify()
    {
        return new Builder(this);
    }

    @NotThreadSafe
    static final class Builder
        extends I18NMessageBundle.Builder
    {
        private final Map<Locale, List<MessageSource>> sources
            = new HashMap<Locale, List<MessageSource>>();

        private Builder(final StaticI18NBundle bundle)
        {
            for (final Map.Entry<Locale, List<MessageSource>> entry:
                bundle.sources.entrySet())
                sources.put(entry.getKey(),
                    new ArrayList<MessageSource>(entry.getValue()));
        }

        @Override
        protected void doAppendSource(final Locale locale,
            final MessageSource source)
        {
        }

        @Override
        protected void doPrependSource(final Locale locale,
            final MessageSource source)
        {
        }

        @Override
        public I18NMessageBundle build()
        {
            return new StaticI18NBundle(this);
        }

        private List<MessageSource> getSourceList(final Locale locale)
        {
            List<MessageSource> ret = sources.get(locale);

            if (ret == null) {
                ret = new ArrayList<MessageSource>();
                sources.put(locale, ret);
            }

            return ret;
        }
    }
}
