package com.github.fge.msgsimple.bundle2;

import com.github.fge.Thawed;
import com.github.fge.msgsimple.provider.MessageSourceProvider;

import java.util.ArrayList;
import java.util.List;

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

    public MessageBundleBuilder appendProvider(
        final MessageSourceProvider provider)
    {
        if (provider == null)
            throw new NullPointerException("cannot append null provider");
        providers.add(provider);
        return this;
    }

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
