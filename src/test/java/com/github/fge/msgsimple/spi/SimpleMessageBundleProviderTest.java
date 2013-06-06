package com.github.fge.msgsimple.spi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class SimpleMessageBundleProviderTest
{
    private SimpleMessageBundleProvider provider;

    @BeforeMethod
    public void init()
    {
        provider = new SimpleMessageBundleProvider();
    }

    @Test
    public void cannotInsertNullKey()
    {
        try {
            provider.put(null, null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "null keys are not allowed");
        }
    }
}
