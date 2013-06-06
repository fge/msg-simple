package com.github.fge.msgsimple.spi;

import com.github.fge.msgsimple.bundle.MessageBundle;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.*;

public final class SimpleMessageBundleProviderTest
{
    private SimpleMessageBundleProvider provider;

    @BeforeMethod
    public void init()
    {
        provider = new SimpleMessageBundleProvider(){};
    }

    @Test
    public void whenNoBundleIsInsertedAnEmptyMapIsReturned()
    {
        assertEquals(provider.getBundles(), Collections.emptyMap());
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

    @Test
    public void cannotInsertNullValue()
    {
        try {
            provider.put("foo", null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "null values are not allowed");
        }
    }

    @Test(dependsOnMethods = "cannotInsertNullValue")
    public void whenABundleIsAddedItCanBeRetrieved()
    {
        final MessageBundle bundle = MessageBundle.newBuilder().freeze();
        provider.put("foo", bundle);
        assertSame(provider.getBundles().get("foo"), bundle);
    }
}
