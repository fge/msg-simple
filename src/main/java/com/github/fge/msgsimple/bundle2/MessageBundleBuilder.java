package com.github.fge.msgsimple.bundle2;

import com.github.fge.Thawed;
import com.github.fge.msgsimple.provider.MessageProvider;

import java.util.ArrayList;
import java.util.List;

public final class MessageBundleBuilder
    implements Thawed<MessageBundle>
{
    final List<MessageProvider> providers = new ArrayList<MessageProvider>();

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
