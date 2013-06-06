package com.github.fge.msgsimple.spi;

import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class MessageBundles
{
    private static final ServiceLoader<MessageBundleProvider> LOADER
        = ServiceLoader.load(MessageBundleProvider.class);

    private static final Map<String, MessageBundle> BUNDLES
        = new HashMap<String, MessageBundle>();

    static {
        for (final MessageBundleProvider provider: LOADER)
            BUNDLES.putAll(provider.getBundles());
    }

    private MessageBundles()
    {
    }
}
