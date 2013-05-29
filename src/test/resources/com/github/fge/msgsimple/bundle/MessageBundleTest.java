package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public final class MessageBundleTest
{
    private static final String KEY1 = "FOO";
    private static final String KEY2 = "BAR";

    private MessageSource source1;
    private MessageSource source2;

    @BeforeMethod
    public void init()
    {
        source1 = mock(MessageSource.class);
        source2 = mock(MessageSource.class);
    }

    @Test
    public void emptyBundleReturnsKeysAsMessages()
    {
        final MessageBundle bundle = new MessageBundle.Builder().build();

        assertEquals(bundle.getKey(KEY1), KEY1);
        assertEquals(bundle.getKey(KEY2), KEY2);
    }

    @Test
    public void firstBundleToContainKeyWins()
    {
        final String msg1 = "foo";
        final String msg2 = "bar";
        when(source1.getMessage(KEY1)).thenReturn(msg1);
        when(source2.getMessage(KEY2)).thenReturn(msg2);

        final MessageBundle bundle = new MessageBundle.Builder()
            .addSource(source1).addSource(source2).build();

        assertEquals(bundle.getKey(KEY1), msg1);
        verify(source1).getMessage(KEY1);
        verify(source2, never()).getMessage(KEY1);

        assertEquals(bundle.getKey(KEY2), msg2);
        verify(source1).getMessage(KEY2);
        verify(source2).getMessage(KEY2);
    }
}
