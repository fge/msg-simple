package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

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
        final List<MessageSource> expected = Arrays.asList(FR_SOURCE);

        final List<MessageSource> l1 = bundle.getSources(FR);
        final List<MessageSource> l2 = bundle.getSources(FR);

        assertEquals(l1, expected);
        assertEquals(l2, expected);
        verify(bundle).tryAndLookup(FR);
    }

    @Test
    public void whenCalledSequentiallyFailedLoadingsOnlyHappenOnce()
        throws IOException
    {
        final List<MessageSource> expected = Collections.emptyList();

        final List<MessageSource> l1 = bundle.getSources(EN_US);
        final List<MessageSource> l2 = bundle.getSources(EN_US);

        assertEquals(l1, expected);
        assertEquals(l2, expected);
        verify(bundle).tryAndLookup(EN_US);
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
