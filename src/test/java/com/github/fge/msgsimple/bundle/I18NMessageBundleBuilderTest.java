package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class I18NMessageBundleBuilderTest
{
    private I18NMessageBundle.Builder builder;
    private MessageSource source;

    @BeforeMethod
    public void init()
    {
        builder = mock(I18NMessageBundle.Builder.class);
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

    @Test
    public void whenParamsAreOKDoAppendIsCalled()
    {
        builder.appendSource(Locale.ROOT, source);
        verify(builder, only()).doAppendSource(Locale.ROOT, source);
    }

    @Test
    public void whenParamsAreOkDoPrependIsCalled()
    {
        builder.prependSource(Locale.ROOT, source);
        verify(builder, only()).doPrependSource(Locale.ROOT, source);
    }
}
