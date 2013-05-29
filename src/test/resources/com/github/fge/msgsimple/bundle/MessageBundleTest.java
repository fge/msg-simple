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
}
