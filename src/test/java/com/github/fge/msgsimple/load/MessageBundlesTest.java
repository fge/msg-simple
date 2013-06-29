package com.github.fge.msgsimple.load;

import com.github.fge.msgsimple.bundle.MessageBundle;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class MessageBundlesTest
{
    @Test
    public void bundleLoadOnlyHappensOnce()
    {
        final MessageBundle bundle1
            = MessageBundles.getBundle(DummyLoader.class);
        final MessageBundle bundle2
            = MessageBundles.getBundle(DummyLoader.class);

        assertSame(bundle1, bundle2);
    }
}
