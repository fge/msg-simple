package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

public final class StaticI18NBundleTest
{
    private StaticI18NBundle.Builder builder;

    private MessageSource source;

    @BeforeMethod
    public void init()
    {
        builder = I18NMessageBundle.newStaticBundle();
        source = mock(MessageSource.class);
    }

    @Test
    public void cannotAppendOrPrependToNullLocale()
    {
        try {
            builder.appendSource(null, source);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "locale is null");
        }

        try {
            builder.prependSource(null, source);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "locale is null");
        }
    }
}
