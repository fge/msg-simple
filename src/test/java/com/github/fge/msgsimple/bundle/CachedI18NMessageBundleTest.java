/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class CachedI18NMessageBundleTest
{
    private static final int TOTAL_THREADS = 50;
    private static final int NTHREADS = 30;

    private static final Locale FR = LocaleUtils.parseLocale("fr");
    private static final Locale EN_US = LocaleUtils.parseLocale("en_US");

    private static final Locale ILLBEHAVED = LocaleUtils.parseLocale("foo");

    private static final String KEY = "key";
    private static final String ROOT_VALUE = "root";
    private static final String FR_VALUE = "aparté";

    private static final MessageSource ROOT_SOURCE = mock(MessageSource.class);
    private static final MessageSource FR_SOURCE = mock(MessageSource.class);

    static {
        when(ROOT_SOURCE.getKey(KEY)).thenReturn(ROOT_VALUE);
        when(FR_SOURCE.getKey(KEY)).thenReturn(FR_VALUE);
    }

    private ExecutorService service;
    private CachedMessageBundle bundle;
    private AtomicInteger intrCount;

    @BeforeMethod
    public void initBundle()
    {
        service = Executors.newFixedThreadPool(NTHREADS);
        intrCount = new AtomicInteger(0);
        bundle = spy(new TestBundle(intrCount));
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
    @Test(dataProvider = "lookups")
    public void existingKeyLookupWorksOK(final Locale locale, final String ret)
        throws InterruptedException, ExecutionException
    {
        final List<Future<String>> list
            = service.invokeAll(createTasks(bundle, KEY, locale));

        for (int i = 0; i < TOTAL_THREADS; i++)
            assertEquals(list.get(i).get(), ret, "value differ at index " + i);
    }

    @Test
    public void onlyOneTaskIsCreatedPerSuccessfulLocaleLookup()
        throws IOException
    {
        for (int i = 0; i < TOTAL_THREADS; i++)
            service.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    bundle.getSources(Locale.ROOT);
                }
            });

        verify(bundle).tryAndLookup(Locale.ROOT);
    }

    @Test
    public void onlyOneTaskIsCreatedPerFailedLocaleLookup()
        throws IOException
    {
        for (int i = 0; i < TOTAL_THREADS; i++)
            service.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    bundle.getSources(EN_US);
                }
            });

        verify(bundle).tryAndLookup(EN_US);
    }

    @Test
    public void whenTimedOutAnotherTaskIsScheduled()
        throws IOException
    {
        bundle.getSources(ILLBEHAVED);
        bundle.getSources(ILLBEHAVED);

        verify(bundle, times(2)).tryAndLookup(ILLBEHAVED);
        assertEquals(intrCount.get(), 2);
    }

    /*
     * We have to do that... Mocks have their limits!
     *
     * Neither the class nor tryAndLookup method can be final, since otherwise
     * we cannot spy.
     */
    private static class TestBundle
        extends CachedMessageBundle
    {
        private final AtomicInteger intrCount;

        private TestBundle(final AtomicInteger intrCount)
        {
            // As always, this is a wild guess... We don't want a test to
            // fail because of that, and we don't want them to take too long
            // either.
            super(250L, TimeUnit.MILLISECONDS);
            this.intrCount = intrCount;
        }

        @Override
        protected MessageSource tryAndLookup(final Locale locale)
            throws IOException
        {
            /*
             * Provide two successes (Locale.ROOT and FR) and one timeout
             * (ILLBEHAVED). All other locales lead to a failure.
             */
            if (locale.equals(ILLBEHAVED))
                try {
                    TimeUnit.MINUTES.sleep(1L);
                } catch (InterruptedException ignored) {
                    intrCount.incrementAndGet();
                }
            if (locale.equals(Locale.ROOT))
                return ROOT_SOURCE;
            if (locale.equals(FR))
                return FR_SOURCE;
            throw new IOException("Too bad!");
        }
    }

    private static Collection<Callable<String>> createTasks(
        final CachedMessageBundle bundle, final String key,
        final Locale locale)
    {
        final List<Callable<String>> ret
            = new ArrayList<Callable<String>>(TOTAL_THREADS);

        for (int i = 0; i < TOTAL_THREADS; i++)
            ret.add(new Callable<String>()
            {
                @Override
                public String call()
                    throws IOException
                {
                    return bundle.getMessage(key, locale);
                }
            });
        return ret;
    }

    @AfterMethod
    public void waitForAllThreads()
    {
        service.shutdown();
    }
}
