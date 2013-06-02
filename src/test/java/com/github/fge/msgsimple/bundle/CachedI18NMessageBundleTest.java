package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class CachedI18NMessageBundleTest
{
    private static final int NTHREADS = 30;

    private static final Locale FR = LocaleUtils.parseLocale("fr");
    private static final Locale EN_US = LocaleUtils.parseLocale("en_US");

    private static final String KEY = "key";
    private static final String ROOT_VALUE = "root";
    private static final String FR_VALUE = "aparté";

    private static final MessageSource ROOT_SOURCE = mock(MessageSource.class);
    private static final MessageSource FR_SOURCE = mock(MessageSource.class);

    static {
        when(ROOT_SOURCE.getKey(KEY)).thenReturn(ROOT_VALUE);
        when(FR_SOURCE.getKey(KEY)).thenReturn(FR_VALUE);
    }

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

    @DataProvider
    public Iterator<Object[]> lookups()
    {
        final List<Object[]> list = new ArrayList<Object[]>();

        Locale locale;
        String value;

        locale = FR;
        value = FR_VALUE;
        list.add(new Object[] { locale, value });

        locale = LocaleUtils.parseLocale("fr_FR");
        value = FR_VALUE;
        list.add(new Object[] { locale, value });

        locale = EN_US;
        value = ROOT_VALUE;
        list.add(new Object[] { locale, value });

        locale = LocaleUtils.parseLocale("ja_JP_JP");
        value = ROOT_VALUE;
        list.add(new Object[] { locale, value });

        locale = Locale.ROOT;
        value = ROOT_VALUE;
        list.add(new Object[] { locale, value });

        return list.iterator();
    }

    /*
     * This is quite a large thread pool size and invocation count, but this is
     * entirely on purpose.
     */
    @Test(threadPoolSize = 50, invocationCount = 10, dataProvider = "lookups")
    public void existingKeyLookupWorksOK(final Locale locale, final String ret)
    {
        assertEquals(bundle.getKey(KEY, locale), ret);
    }

    @Test
    public void onlyOneTaskIsCreatedPerSuccessfulLocaleLookup()
        throws IOException
    {
        /*
         * Create a thread pool; make it access the same resource repeatedly;
         * check that the tryAndLookup() method is only ever called once.
         */
        final ExecutorService service = Executors.newFixedThreadPool(NTHREADS);

        for (int i = 0; i < NTHREADS; i++)
            service.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    bundle.getSources(Locale.ROOT);
                }
            });

        service.shutdown();
        verify(bundle).tryAndLookup(Locale.ROOT);
    }

    @Test
    public void onlyOneTaskIsCreatedPerFailedLocaleLookup()
        throws IOException
    {
        /*
         * Create a thread pool; make it access the same resource repeatedly;
         * check that the tryAndLookup() method is only ever called once.
         */
        final ExecutorService service = Executors.newFixedThreadPool(NTHREADS);

        for (int i = 0; i < NTHREADS; i++)
            service.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    bundle.getSources(EN_US);
                }
            });

        service.shutdown();
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
