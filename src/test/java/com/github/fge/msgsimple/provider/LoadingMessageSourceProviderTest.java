package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class LoadingMessageSourceProviderTest
{
    private LoadingMessageSourceProvider.Builder builder;
    private MessageSourceLoader loader;
    private MessageSource defaultSource;
    private MessageSource source;

    @BeforeMethod
    public void init()
    {
        builder = LoadingMessageSourceProvider.newBuilder();
        loader = mock(MessageSourceLoader.class);
        defaultSource = mock(MessageSource.class);
        source = mock(MessageSource.class);
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
        final MessageSourceProvider provider = builder.setLoader(loader).build();

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

    @Test(dependsOnMethods = {
        "loaderIsUsedWhenItIsSet",
        "cannotProvideNullDefaultSource"
    })
    public void defaultSourceIsReturnedWhenLoaderHasNoMatch()
    {
        final MessageSourceProvider provider = builder.setLoader(loader)
            .setDefaultSource(defaultSource).build();

        assertSame(provider.getMessageSource(Locale.ROOT), defaultSource);
    }

    @Test(dependsOnMethods = {
        "loaderIsUsedWhenItIsSet",
        "cannotProvideNullDefaultSource"
    })
    public void defaultSourceIsReturnedIfLoaderThrowsAnException()
        throws IOException
    {
        when(loader.load(any(Locale.class))).thenThrow(new IOException());
        final MessageSourceProvider provider = builder.setLoader(loader)
            .setDefaultSource(defaultSource).build();

        assertSame(provider.getMessageSource(Locale.ROOT), defaultSource);
    }

    @Test(dependsOnMethods = "loaderIsUsedWhenItIsSet")
    public void loadingIsOnlyCalledOnce()
        throws IOException, InterruptedException, ExecutionException
    {
        final Locale locale = Locale.ROOT;
        when(loader.load(locale)).thenReturn(source);

        final MessageSourceProvider provider = builder.setLoader(loader).build();

        final int nThreads = 30;
        final ExecutorService service = Executors.newFixedThreadPool(nThreads);
        final List<Callable<MessageSource>> callables
            = new ArrayList<Callable<MessageSource>>(nThreads);

        for (int i = 0; i < nThreads; i++)
            callables.add(new Callable<MessageSource>()
            {
                @Override
                public MessageSource call()
                    throws IOException
                {
                    return provider.getMessageSource(Locale.ROOT);
                }
            });

        final List<Future<MessageSource>> results = service.invokeAll(callables);
        service.shutdown();

        for (int i = 0; i < nThreads; i++)
            assertSame(results.get(i).get(), source);

        verify(loader, only()).load(Locale.ROOT);
    }

    @Test
    public void cannotSetZeroOrNegativeTimeout()
    {
        try {
            builder.setTimeout(0L, null);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "timeout must be greater than 0");
        }

        try {
            builder.setTimeout(-1L, null);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "timeout must be greater than 0");
        }
    }

    @Test(dependsOnMethods = "cannotSetZeroOrNegativeTimeout")
    public void cannotSetNullTimeUnit()
    {
        try {
            builder.setTimeout(1L, null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "time unit must not be null");
        }
    }

    @Test(dependsOnMethods = {
        "loaderIsUsedWhenItIsSet",
        "cannotSetNullTimeUnit"
    })
    public void whenLoadTimesOutDefaultSourceIsReturned()
        throws IOException
    {
        when(loader.load(Locale.ROOT)).then(new Answer<MessageSource>()
        {
            @Override
            public MessageSource answer(final InvocationOnMock invocation)
                throws IOException, InterruptedException
            {
                TimeUnit.SECONDS.sleep(2L);
                return source;
            }
        }).thenReturn(source);

        final MessageSourceProvider provider
            = builder.setLoader(loader).setTimeout(250L, TimeUnit.MILLISECONDS)
            .setDefaultSource(defaultSource).build();

        assertSame(provider.getMessageSource(Locale.ROOT), defaultSource);
        assertSame(provider.getMessageSource(Locale.ROOT), source);
    }
}
