package com.github.fge.msgsimple.serviceloader;

import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class MessageBundles
{
    // Yes, "static" but not "final" :/
    private static MessageBundles INSTANCE;

    private final Map<Class<? extends MessageBundleProvider>, MessageBundle>
        bundles = new IdentityHashMap<Class<? extends MessageBundleProvider>, MessageBundle>();

    private MessageBundles()
    {
        final ServiceLoader<MessageBundleProvider> serviceLoader
            = ServiceLoader.load(MessageBundleProvider.class);
        for (final MessageBundleProvider provider: serviceLoader)
            bundles.put(provider.getClass(), provider.getBundle());
    }

    public static synchronized MessageBundle forClass(
        final Class<? extends MessageBundleProvider> c)
    {
        // No choice... Bah, it's a one shot. And it's simple.
        if (INSTANCE == null)
            INSTANCE = new MessageBundles();
        return INSTANCE.bundles.get(c);
    }
}
