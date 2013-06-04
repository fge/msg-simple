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

    @Override
    public MessageBundle freeze()
    {
        return new MessageBundle(this);
    }
}
