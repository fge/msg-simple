package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class MessageBundleTest
{
    private static final String KEY1 = "FOO";
    private static final String KEY2 = "BAR";

    private MessageSource source1;
    private MessageSource source2;
    private MessageBundle bundle;

    @BeforeMethod
    public void init()
    {
        source1 = mock(MessageSource.class);
        source2 = mock(MessageSource.class);
        bundle = MessageBundle.newBundle().appendSource(source1)
            .appendSource(source2).build();
    }

    @Test
    public void keyIsReturnedWhenNotDefinedInAnySource()
    {
        assertEquals(bundle.getKey(KEY1), KEY1);
        assertEquals(bundle.getKey(KEY2), KEY2);
    }

    @Test
    public void firstBundleToContainKeyWins()
    {
        final String msg1 = "foo";
        final String msg2 = "bar";
        when(source1.getKey(KEY1)).thenReturn(msg1);
        when(source2.getKey(KEY2)).thenReturn(msg2);

        assertEquals(bundle.getKey(KEY1), msg1);
        verify(source1).getKey(KEY1);
        verify(source2, never()).getKey(KEY1);

        assertEquals(bundle.getKey(KEY2), msg2);
        verify(source1).getKey(KEY2);
        verify(source2).getKey(KEY2);
    }

    @Test
    public void cannotAppendNullMessageSource()
    {
        try {
            MessageBundle.newBundle().appendSource(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "cannot append null message source");
        }
    }

    @Test
    public void cannotPrependNullMessageSource()
    {
        try {
            MessageBundle.newBundle().prependSource(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "cannot prepend null message source");
        }
    }

    @Test
    public void prependingSourceWorksAsExpected()
    {
        final String value = "meh";
        final MessageSource source = mock(MessageSource.class);
        when(source.getKey(KEY1)).thenReturn(value);

        final MessageBundle bundle2 = bundle.modify().prependSource(source)
            .build();

        assertEquals(bundle2.getKey(KEY1), value);
    }

    @Test
    public void cannotQueryNullKey()
    {
        try {
            bundle.getKey(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "cannot query null key");
        }
    }
}
