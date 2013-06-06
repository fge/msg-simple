package com.github.fge.msgsimple.spi;

import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.Collections;
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
            // Just in case...
            return Collections.unmodifiableMap(map);
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
