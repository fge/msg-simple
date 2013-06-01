package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Locale;

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
    public void cannotAppendToNullLocale()
    {
        try {
            builder.appendSource(null, source);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "locale is null");
        }
    }

    @Test
    public void cannotPrependToNullLocale()
    {
        try {
            builder.prependSource(null, source);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "locale is null");
        }
    }

    @Test
    public void cannotAppendNullSource()
    {
        try {
            builder.appendSource(Locale.ROOT, null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "message source is null");
        }
    }

    @Test
    public void cannotPrependNullSource()
    {
        try {
            builder.prependSource(Locale.ROOT, null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "message source is null");
        }
    }
}
