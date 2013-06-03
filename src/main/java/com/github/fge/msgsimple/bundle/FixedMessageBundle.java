package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class FixedMessageBundle
    extends MessageBundle
{
    private final Map<Locale, List<MessageSource>> sources
        = new HashMap<Locale, List<MessageSource>>();

    private FixedMessageBundle(final Builder builder)
    {
        List<MessageSource> list;

        for (final Map.Entry<Locale, List<MessageSource>> entry:
            builder.sources.entrySet()) {
            list = new ArrayList<MessageSource>(entry.getValue());
            sources.put(entry.getKey(), Collections.unmodifiableList(list));
        }
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    @Override
    protected List<MessageSource> getSources(final Locale locale)
    {
        final List<MessageSource> ret = sources.get(locale);
        // We can return ret directly: on build, it was wrapped with
        // Collections.unmodifiableList()
        return ret == null ? Collections.<MessageSource>emptyList() : ret;
    }

    public static Builder modify(final MessageBundle bundle)
    {
        if (!(bundle instanceof FixedMessageBundle))
            throw new IllegalStateException("bundle is not modifyable" +
                " (not a FixedMessageBundle)");
        return new Builder((FixedMessageBundle) bundle);
    }

    @NotThreadSafe
    public static final class Builder
    {
        private final Map<Locale, List<MessageSource>> sources
            = new HashMap<Locale, List<MessageSource>>();

        Builder()
        {
        }

        private Builder(final FixedMessageBundle bundle)
        {
            for (final Map.Entry<Locale, List<MessageSource>> entry:
                bundle.sources.entrySet())
                sources.put(entry.getKey(),
                    new ArrayList<MessageSource>(entry.getValue()));
        }

        public Builder appendSource(final Locale locale,
            final MessageSource source)
        {
            if (locale == null)
                throw new NullPointerException("locale is null");
            if (source == null)
                throw new NullPointerException("message source is null");
            getSourceList(locale).add(source);
            return this;
        }

        public Builder prependSource(final Locale locale,
            final MessageSource source)
        {
            if (locale == null)
                throw new NullPointerException("locale is null");
            if (source == null)
                throw new NullPointerException("message source is null");
            getSourceList(locale).add(0, source);
            return this;
        }

        public MessageBundle build()
        {
            return new FixedMessageBundle(this);
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
