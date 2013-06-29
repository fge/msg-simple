package com.github.fge.msgsimple.load;

import com.github.fge.msgsimple.bundle.MessageBundle;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;

public final class MessageBundlesTest
{
    private static final AtomicInteger COUNT = new AtomicInteger(0);
    private static final MessageBundle BUNDLE
        = MessageBundle.newBuilder().freeze();

    @Test
    public void bundleLoadOnlyHappensOnce()
    {
        final MessageBundle bundle1
            = MessageBundles.getBundle(DummyLoader.class);
        final MessageBundle bundle2
            = MessageBundles.getBundle(DummyLoader.class);

        assertSame(bundle1, bundle2);
        assertSame(bundle1, BUNDLE);
        assertEquals(COUNT.get(), 1);
    }

    public static final class DummyLoader
        implements MessageBundleLoader
    {

        @Override
        public MessageBundle getBundle()
        {
            COUNT.incrementAndGet();
            return BUNDLE;
        }
    }
}
