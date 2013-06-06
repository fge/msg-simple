package com.github.fge.msgsimple.spi;

import com.github.fge.msgsimple.bundle.MessageBundle;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class MessageBundlesTest
{
    private MessageBundles.MapBuilder builder;
    private Map<String, MessageBundle> bundles;
    private MessageBundleProvider provider;

    @BeforeMethod
    public void init()
    {
        builder = new MessageBundles.MapBuilder();
        bundles = new HashMap<String, MessageBundle>();
        provider = mock(MessageBundleProvider.class);
        when(provider.getBundles()).thenReturn(bundles);
    }

    @Test
    public void cannotInsertNullKey()
    {
        bundles.put(null, null);
        try {
            builder.loadFrom(provider);
            fail("No exception thrown!");
        } catch (MessageBundles.BundleLoadingException e) {
            assertEquals(e.getMessage(), "null bundle names are not allowed");
        }
    }

    @Test
    public void cannotInsertNullBundle()
    {
        bundles.put("foo", null);
        try {
            builder.loadFrom(provider);
            fail("No exception thrown!");
        } catch (MessageBundles.BundleLoadingException e) {
            assertEquals(e.getMessage(), "null bundles are not allowed");
        }
    }

    @Test
    public void cannotInsertSameNameTwice()
    {
        // Can't mock MessageBundle, it's final by design, so...
        bundles.put("foo", MessageBundle.newBuilder().freeze());
        try {
            builder.loadFrom(provider);
            builder.loadFrom(provider);
            fail("No exception thrown!");
        } catch (MessageBundles.BundleLoadingException e) {
            assertEquals(e.getMessage(), "there is already a bundle with " +
                "name \"foo\"");
        }
    }

    @Test
    public void insertedBundlesAreRetrievable()
        throws MessageBundles.BundleLoadingException
    {
        final MessageBundle bundle = MessageBundle.newBuilder().freeze();
        bundles.put("foo", bundle);
        builder.loadFrom(provider);
        assertSame(builder.getMap().get("foo"), bundle);
    }
}
