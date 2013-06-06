package com.github.fge.msgsimple.spi;

import com.github.fge.msgsimple.bundle.MessageBundle;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@NotThreadSafe
public final class SimpleMessageBundleProvider
    implements MessageBundleProvider
{
    private final Map<String, MessageBundle> bundles
        = new HashMap<String, MessageBundle>();

    @Override
    public Map<String, MessageBundle> getBundles()
    {
        return Collections.unmodifiableMap(bundles);
    }

    public void put(final String key, final MessageBundle bundle)
    {
        if (key == null)
            throw new NullPointerException("null keys are not allowed");
        if (bundle == null)
            throw new NullPointerException("null values are not allowed");
        bundles.put(key, bundle);
    }
}
