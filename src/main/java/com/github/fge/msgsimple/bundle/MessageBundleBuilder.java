package com.github.fge.msgsimple.bundle;

import com.github.fge.Thawed;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.provider.StaticMessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.spi.MessageBundles;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Builder class for a message bundle
 *
 * <p>You cannot instantiate this class directly: use {@link
 * MessageBundle#newBuilder()}.</p>
 */
public final class MessageBundleBuilder
    implements Thawed<MessageBundle>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getByName("com.github.fge:msg-simple");

    final List<MessageSourceProvider> providers
        = new ArrayList<MessageSourceProvider>();

    MessageBundleBuilder()
    {
    }

    MessageBundleBuilder(final MessageBundle bundle)
    {
        providers.addAll(bundle.providers);
    }

    /**
     * Append a message provider
     *
     * @param provider the provider
     * @throws NullPointerException provider is null
     * @return this
     */
    public MessageBundleBuilder appendProvider(
        final MessageSourceProvider provider)
    {
        if (provider == null)
            throw new NullPointerException(BUNDLE.getMessage("cfg.nullProvider"));
        providers.add(provider);
        return this;
    }

    /**
     * Prepend a message provider
     *
     * @param provider the provider
     * @throws NullPointerException provider is null
     * @return this
     */
    public MessageBundleBuilder prependProvider(
        final MessageSourceProvider provider)
    {
        if (provider == null)
            throw new NullPointerException(BUNDLE.getMessage("cfg.nullProvider"));
        providers.add(0, provider);
        return this;
    }

    /**
     * Convenience method to append a single-source provider
     *
     * @param source the message source
     * @return this
     * @see StaticMessageSourceProvider#withSingleSource(MessageSource)
     */
    public MessageBundleBuilder appendSource(final MessageSource source)
    {
        final MessageSourceProvider provider
            = StaticMessageSourceProvider.withSingleSource(source);
        providers.add(provider);
        return this;
    }

    /**
     * Convenience method to prepend a single-source provider
     *
     * @param source the message source
     * @return this
     * @see StaticMessageSourceProvider#withSingleSource(MessageSource)
     */
    public MessageBundleBuilder prependSource(final MessageSource source)
    {
        final MessageSourceProvider provider
            = StaticMessageSourceProvider.withSingleSource(source);
        providers.add(0, provider);
        return this;
    }

    /**
     * Convenience method to append a single-source provider for a given locale
     *
     * @param source the message source
     * @return this
     * @see StaticMessageSourceProvider#withSingleSource(Locale, MessageSource)
     */
    public MessageBundleBuilder appendSource(final Locale locale,
        final MessageSource source)
    {
        final MessageSourceProvider provider
            = StaticMessageSourceProvider.withSingleSource(locale, source);
        providers.add(provider);
        return this;
    }

    /**
     * Convenience method to prepend a single-source provider for a given locale
     *
     * @param source the message source
     * @return this
     * @see StaticMessageSourceProvider#withSingleSource(Locale, MessageSource)
     */
    public MessageBundleBuilder prependSource(final Locale locale,
        final MessageSource source)
    {
        final MessageSourceProvider provider
            = StaticMessageSourceProvider.withSingleSource(locale, source);
        providers.add(0, provider);
        return this;
    }

    @Override
    public MessageBundle freeze()
    {
        return new MessageBundle(this);
    }
}
