package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;

@ThreadSafe
public final class MessageBundle
{
    private final List<MessageSource> sources;

    private MessageBundle(final Builder builder)
    {
        sources = new ArrayList<MessageSource>(builder.sources);
    }

    public String getKey(final String key)
    {
        if (key == null)
            throw new NullPointerException("cannot query null key");

        String ret;

        for (final MessageSource source: sources) {
            ret = source.getMessage(key);
            if (ret != null)
                return ret;
        }

        return key;
    }

    public Builder copy()
    {
        return new Builder(this);
    }

    @NotThreadSafe
    public static final class Builder
    {
        private final List<MessageSource> sources
            = new ArrayList<MessageSource>();

        public Builder()
        {
        }

        private Builder(final MessageBundle bundle)
        {
            sources.addAll(bundle.sources);
        }

        public Builder appendSource(final MessageSource source)
        {
            if (source == null)
                throw new NullPointerException("cannot add null message source");
            sources.add(source);
            return this;
        }

        public MessageBundle build()
        {
            return new MessageBundle(this);
        }
    }
}
