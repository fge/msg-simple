package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertSame;

public final class StaticMessageSourceProviderTest
{
    private MessageSource source;
    private StaticMessageSourceProvider.Builder builder;

    @BeforeMethod
    public void init()
    {
        source = mock(MessageSource.class);
        builder = StaticMessageSourceProvider.newBuilder();
    }

    @Test
    public void defaultSourceIsProvidedIfNoOtherSourceIsPresent()
    {
        builder.setDefaultSource(source);
        assertSame(builder.build().getMessageSource(Locale.ROOT), source);
    }
}
