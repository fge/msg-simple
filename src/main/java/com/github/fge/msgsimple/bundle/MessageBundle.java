package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Simple, non-localized message bundle
 *
 * <p>In order to create a bundle, you need to perform the following steps:</p>
 *
 * <ul>
 *     <li>create one or more {@link MessageSource}s;</li>
 *     <li>create a new bundle builder, using {@link
 *     MessageBundle#newBundle()}</li>;
 *     <li>append/prepend your message sources using {@link
 *     MessageBundle.Builder#appendSource(MessageSource)} or {@link
 *     MessageBundle.Builder#prependSource(MessageSource)};</li>
 *     <li>build the final bundle using {@link
 *     MessageBundle.Builder#build()}.</li>
 * </ul>
 */
@ThreadSafe
public final class MessageBundle
{
    private final List<MessageSource> sources;

    private MessageBundle(final Builder builder)
    {
        sources = new ArrayList<MessageSource>(builder.sources);
    }

    /**
     * Get the message matching that key
     *
     * <p>This method looks up all declared message sources for a string
     * matching this key. If the given key was not found in any message source,
     * the key itself is returned.</p>
     *
     * <p>This is therefore a very different behaviour from what you would
     * expect from a {@link ResourceBundle}, which throws an (unchecked!)
     * exception if the key cannot be found. This also means that this method
     * <b>never</b> returns {@code null}.</p>
     *
     * @param key the key to return
     * @return the first matching message for that key; the key itself if no
     * source has a matching entry for this key
     * @see MessageSource
     * @see MessageBundle.Builder
     */
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

    /**
     * Return a modifable version of this bundle
     *
     * @return a {@link Builder} with this bundle's message sources
     */
    public Builder modify()
    {
        return new Builder(this);
    }

    /**
     * Return a modifable version of this bundle
     *
     * @return a {@link Builder} with this bundle's message sources
     * @deprecated use {@link #modify()} instead
     */
    @Deprecated
    public Builder copy()
    {
        return modify();
    }

    /**
     * Create a new, empty bundle builder
     *
     * @return a {@link Builder}
     */
    public static Builder newBundle()
    {
        return new Builder();
    }

    @NotThreadSafe
    public static final class Builder
    {
        private final List<MessageSource> sources
            = new ArrayList<MessageSource>();

        /**
         * Constructor
         *
         * @deprecated use {@link #newBundle()} instead
         */
        @Deprecated
        public Builder()
        {
        }

        private Builder(final MessageBundle bundle)
        {
            sources.addAll(bundle.sources);
        }

        /**
         * Append one message source to the already registered sources
         *
         * @param source the source to append
         * @return this
         */
        public Builder appendSource(final MessageSource source)
        {
            if (source == null)
                throw new NullPointerException("cannot append " +
                    "null message source");
            sources.add(source);
            return this;
        }

        /**
         * Prepend one message source to the already registered soruces
         *
         * @param source the source to prepend
         * @return this
         */
        public Builder prependSource(final MessageSource source)
        {
            if (source == null)
                throw new NullPointerException("cannot prepend " +
                    "null message source");
            sources.add(0, source);
            return this;
        }

        /**
         * Build the bundle
         *
         * @return a newly created {@link MessageBundle}
         */
        public MessageBundle build()
        {
            return new MessageBundle(this);
        }
    }
}
