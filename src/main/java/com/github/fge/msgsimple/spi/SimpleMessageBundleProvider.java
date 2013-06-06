package com.github.fge.msgsimple.spi;

import com.github.fge.msgsimple.bundle.MessageBundle;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@NotThreadSafe
public abstract class SimpleMessageBundleProvider
    implements MessageBundleProvider
{
    private final Map<String, MessageBundle> bundles
        = new HashMap<String, MessageBundle>();

    @Override
    public final Map<String, MessageBundle> getBundles()
    {
        return Collections.unmodifiableMap(bundles);
    }

    final void put(final String key, final MessageBundle bundle)
    {
        if (key == null)
            throw new NullPointerException("null keys are not allowed");
        if (bundle == null)
            throw new NullPointerException("null values are not allowed");
        bundles.put(key, bundle);
    }
}
