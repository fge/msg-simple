package com.github.fge.msgsimple.bundle;

import com.github.fge.Thawed;
import com.github.fge.msgsimple.provider.MessageSourceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for a message bundle
 *
 * <p>You cannot instantiate this class directly: use {@link
 * MessageBundle#newBuilder()}.</p>
 */
public final class MessageBundleBuilder
    implements Thawed<MessageBundle>
{
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
            throw new NullPointerException("cannot append null provider");
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
            throw new NullPointerException("cannot prepend null provider");
        providers.add(0, provider);
        return this;
    }

    @Override
    public MessageBundle freeze()
    {
        return new MessageBundle(this);
    }
}
