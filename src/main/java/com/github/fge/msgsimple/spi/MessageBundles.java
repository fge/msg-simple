package com.github.fge.msgsimple.spi;

import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class MessageBundles
{
    // Yes, "static" but not "final" :/
    private static MessageBundles INSTANCE;

    private final Map<String, MessageBundle> bundles;

    private MessageBundles()
        throws LoadingException
    {
        final ServiceLoader<MessageBundleProvider> serviceLoader
            = ServiceLoader.load(MessageBundleProvider.class);
        final Loader loader = new Loader();
        for (final MessageBundleProvider provider: serviceLoader)
            loader.loadFrom(provider);
        bundles = loader.getMap();
    }

    public static synchronized MessageBundle getByName(final String name)
    {
        // No choice... Bah, it's a one shot. And it's simple.
        if (INSTANCE == null)
            try {
                INSTANCE = new MessageBundles();
            } catch (LoadingException e) {
                throw new ExceptionInInitializerError(e);
            }
        return INSTANCE.bundles.get(name);
    }

    /**
     * Visible for testing purposes only! Do not instantiate!
     */
    static final class Loader
    {
        private final Map<String, MessageBundle> map
            = new HashMap<String, MessageBundle>();

        void loadFrom(final MessageBundleProvider provider)
            throws LoadingException
        {
            String name;
            MessageBundle bundle;

            for (final Map.Entry<String, MessageBundle> entry:
                provider.getBundles().entrySet()) {
                name = entry.getKey();
                bundle = entry.getValue();
                if (name == null)
                    throw new LoadingException("null bundle names are " +
                        "not allowed");
                if (bundle == null)
                    throw new LoadingException("null bundles are not " +
                        "allowed");
                if (map.put(name, bundle) != null)
                    throw new LoadingException("there is already a " +
                        "bundle with name \"" + name + '"');
            }
        }

        Map<String, MessageBundle> getMap()
        {
            return new HashMap<String, MessageBundle>(map);
        }
    }

    /**
     * Visible for testing purposes only! Do not use!
     */
    static final class LoadingException
        extends Exception
    {
        LoadingException(final String message)
        {
            super(message);
        }
    }
}
