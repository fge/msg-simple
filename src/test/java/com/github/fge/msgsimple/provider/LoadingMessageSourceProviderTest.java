package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.provider.load.MessageSourceLoader;
import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class LoadingMessageSourceProviderTest
{
    private LoadingMessageSourceProvider.Builder builder;
    private MessageSourceLoader loader;
    private MessageSource defaultSource;

    @BeforeMethod
    public void init()
    {
        builder = LoadingMessageSourceProvider.newBuilder();
        loader = mock(MessageSourceLoader.class);
    }

    @Test
    public void cannotBuildWithoutALoader()
    {
        try {
            builder.build();
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "no loader has been provided");
        }
    }

    @Test
    public void cannotSetNullLoader()
    {
        try {
            builder.setLoader(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "loader cannot be null");
        }
    }

    @Test(dependsOnMethods = "cannotSetNullLoader")
    public void loaderIsUsedWhenItIsSet()
        throws IOException
    {
        final MessageSourceProvider provider
            = builder.setLoader(loader).build();

        final Locale locale = Locale.ROOT;

        provider.getMessageSource(locale);
        verify(loader, only()).load(locale);
    }

    @Test
    public void cannotProvideNullDefaultSource()
    {
        try {
            builder.setDefaultSource(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "default source cannot be null");
        }
    }
}
