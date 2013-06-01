package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.source.MessageSource;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Locale;

import static org.mockito.Mockito.*;

public final class CachedI18NMessageBundleTest
{
    private static final Locale FR = LocaleUtils.parseLocale("fr");
    private static final Locale EN_US = LocaleUtils.parseLocale("en_US");

    private static final MessageSource ROOT_SOURCE = mock(MessageSource.class);
    private static final MessageSource FR_SOURCE = mock(MessageSource.class);

    private CachedI18NMessageBundle bundle;

    @BeforeMethod
    public void init()
    {
        bundle = spy(new TestBundle());
    }

    @Test
    public void whenCalledSequentiallySuccessfulLoadingsOnlyHappenOnce()
        throws IOException
    {
        bundle.getKey("", FR);
        bundle.getKey("", FR);

        final InOrder order = inOrder(bundle);
        order.verify(bundle, times(1)).tryAndLookup(FR);
        order.verify(bundle, times(1)).tryAndLookup(Locale.ROOT);
    }

    @Test
    public void whenCalledSequentiallyFailedLoadingsOnlyHappenOnce()
        throws IOException
    {
        bundle.getKey("", EN_US);
        bundle.getKey("", EN_US);

        final InOrder order = inOrder(bundle);
        order.verify(bundle, times(1)).tryAndLookup(EN_US);
        order.verify(bundle, times(1)).tryAndLookup(Locale.ROOT);
    }

    /*
     * We have to do that... Mocks have their limits!
     *
     * Neither the class nor tryAndLookup method can be final, since otherwise
     * we cannot spy.
     */
    private static class TestBundle
        extends CachedI18NMessageBundle
    {
        @Override
        protected MessageSource tryAndLookup(final Locale locale)
            throws IOException
        {
            /*
             * Provide two successes (Locale.ROOT and FR). All other locales
             * lead to a failure.
             */
            if (locale.equals(Locale.ROOT))
                return ROOT_SOURCE;
            if (locale.equals(FR))
                return FR_SOURCE;
            throw new IOException("Too bad!");
        }
    }
}
