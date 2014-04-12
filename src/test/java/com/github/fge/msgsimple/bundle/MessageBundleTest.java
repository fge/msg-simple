/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.InternalBundle;
import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class MessageBundleTest
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

    private MessageBundleBuilder builder;

    private MessageSourceProvider provider;
    private MessageSourceProvider provider2;

    private MessageSource source;
    private MessageSource source2;

    @BeforeMethod
    public void init()
    {
        builder = MessageBundle.newBuilder();

        provider = mock(MessageSourceProvider.class);
        provider2 = mock(MessageSourceProvider.class);

        source = mock(MessageSource.class);
        source2 = mock(MessageSource.class);
    }

    @Test
    public void cannotAppendNullProvider()
    {
        try {
            builder.appendProvider(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.nullProvider"));
        }
    }

    @Test(dependsOnMethods = "cannotAppendNullProvider")
    public void appendedProvidersAreUsed()
    {
        final MessageBundle bundle = builder.appendProvider(provider).freeze();
        bundle.getMessage(Locale.ROOT, "foo");
        verify(provider, only()).getMessageSource(Locale.ROOT);
    }

    @Test(dependsOnMethods = "appendedProvidersAreUsed")
    public void appendedProvidersAreUsedInOrderOfInsertion()
    {
        final MessageBundle bundle = builder.appendProvider(provider)
            .appendProvider(provider2).freeze();

        bundle.getMessage(Locale.ROOT, "foo");

        final InOrder inOrder = inOrder(provider, provider2);

        inOrder.verify(provider).getMessageSource(Locale.ROOT);
        inOrder.verify(provider2).getMessageSource(Locale.ROOT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void cannotPrependNullProvider()
    {
        try {
            builder.prependProvider(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.nullProvider"));
        }
    }

    @Test(dependsOnMethods = {
        "cannotPrependNullProvider",
        "appendedProvidersAreUsed"
    })
    public void prependedProvidersAreUsedFirst()
    {
        final MessageBundle bundle = builder.appendProvider(provider)
            .prependProvider(provider2).freeze();

        bundle.getMessage(Locale.ROOT, "foo");

        final InOrder inOrder = inOrder(provider, provider2);

        inOrder.verify(provider2).getMessageSource(Locale.ROOT);
        inOrder.verify(provider).getMessageSource(Locale.ROOT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = "appendedProvidersAreUsed")
    public void localesAreAllTriedUntilRootLocale()
    {
        final MessageBundle bundle = builder.appendProvider(provider).freeze();

        final Locale locale = LocaleUtils.parseLocale("ja_JP_JP");

        final InOrder inOrder = inOrder(provider);

        bundle.getMessage(locale, "foo");

        for (final Locale l: LocaleUtils.getApplicable(locale))
            inOrder.verify(provider).getMessageSource(l);

        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = "localesAreAllTriedUntilRootLocale")
    public void sourceIsQueriedForKeyWhenFound()
    {
        final Locale locale1 = LocaleUtils.parseLocale("ja_JP_JP");
        final Locale locale2 = LocaleUtils.parseLocale("ja_JP");
        final Locale locale3 = LocaleUtils.parseLocale("ja");

        final String key = "key";

        when(provider.getMessageSource(locale1)).thenReturn(source);
        when(provider.getMessageSource(locale3)).thenReturn(source2);

        final MessageBundle bundle = builder.appendProvider(provider).freeze();

        bundle.getMessage(locale1, key);

        final InOrder inOrder = inOrder(provider, source, source2);

        inOrder.verify(provider).getMessageSource(locale1);
        inOrder.verify(source).getKey(key);
        inOrder.verify(provider).getMessageSource(locale2);
        inOrder.verify(provider).getMessageSource(locale3);
        inOrder.verify(source2).getKey(key);
        inOrder.verify(provider).getMessageSource(Locale.ROOT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = {
        "appendedProvidersAreUsed",
        "localesAreAllTriedUntilRootLocale"
    })
    public void providersAreAllTriedForOneLocaleBeforeTryingNextOne()
    {
        final MessageBundle bundle = builder.appendProvider(provider)
            .appendProvider(provider2).freeze();

        final Locale locale = LocaleUtils.parseLocale("ja_JP_JP");

        bundle.getMessage(locale, "foo");

        final InOrder inOrder = inOrder(provider, provider2);

        for (final Locale l: LocaleUtils.getApplicable(locale)) {
            inOrder.verify(provider).getMessageSource(l);
            inOrder.verify(provider2).getMessageSource(l);
        }

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void whenNoMessageIsFoundKeyIsReturned()
    {
        assertEquals(builder.freeze().getMessage(Locale.ROOT, "foo"), "foo");
    }

    @Test(dependsOnMethods = {
        "appendedProvidersAreUsed",
        "localesAreAllTriedUntilRootLocale"
    })
    public void whenKeyIsFoundMessageIsReturned()
    {
        final Locale locale = Locale.CHINA;
        final String key = "key";
        final String value = "value";

        when(source.getKey(key)).thenReturn(value);
        when(provider.getMessageSource(locale)).thenReturn(source);

        final MessageBundle bundle = builder.appendProvider(provider).freeze();

        final String msg = bundle.getMessage(locale, key);

        assertEquals(msg, value);
    }

    @Test(dependsOnMethods = "whenKeyIsFoundMessageIsReturned")
    public void whenKeyIsFoundNoFurtherProvidersOrSourcesAreTried()
    {
        final Locale locale1 = LocaleUtils.parseLocale("fr_FR");
        final Locale locale2 = LocaleUtils.parseLocale("fr");
        final String key = "key";
        final String value = "value";

        when(provider.getMessageSource(locale1)).thenReturn(source);
        when(source2.getKey(key)).thenReturn(value);
        when(provider2.getMessageSource(locale2)).thenReturn(source2);

        final MessageBundle bundle = builder.appendProvider(provider)
            .appendProvider(provider2).freeze();

        final InOrder inOrder = inOrder(provider, source, provider2, source2);

        bundle.getMessage(locale1, key);

        inOrder.verify(provider).getMessageSource(locale1);
        inOrder.verify(source).getKey(key);
        inOrder.verify(provider2).getMessageSource(locale1);
        inOrder.verify(provider).getMessageSource(locale2);
        inOrder.verify(source2).getKey(key);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void cannotQueryNullKey()
    {
        try {
            builder.freeze().getMessage(Locale.ROOT, null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("query.nullKey"));
        }
    }

    @Test
    public void cannotQueryNullLocale()
    {
        try {
            builder.freeze().getMessage(null, "foo");
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("query.nullLocale"));
        }
    }

    @DataProvider
    public Iterator<Object[]> printfData()
    {
        final List<Object[]> list = new ArrayList<Object[]>();

        Locale locale;
        String pattern;
        Object[] params;
        String ret;

        locale = Locale.ROOT;
        pattern = "Hello %s";
        params = new Object[] { "World" };
        ret = "Hello World";
        list.add(new Object[] { locale, pattern, ret, params });

        locale = Locale.ROOT;
        pattern = "Hello %s";
        params = new Object[] { null };
        ret = "Hello null";
        list.add(new Object[] { locale, pattern, ret, params });

        locale = Locale.ROOT;
        pattern = "Hello %s";
        params = new Object[0];
        ret = "Hello %s";
        list.add(new Object[] { locale, pattern, ret, params });

        locale = LocaleUtils.parseLocale("fr_FR");
        pattern = "La %s du %s";
        params = new Object[] { "peur", "gendarme" };
        ret = "La peur du gendarme";
        list.add(new Object[] { locale, pattern, ret, params });

        locale = Locale.CHINA;
        pattern = "Plus on est de %s, moins il y a de %s !";
        params = new Object[] { "fous", "riz", "Mao" };
        ret = "Plus on est de fous, moins il y a de riz !";
        list.add(new Object[] { locale, pattern, ret, params });

        return list.iterator();
    }

    /*
     * Note: despite the bug having supposedly been fixed in TestNG 6.1,
     * @DataProvider doesn't like varargs methods...
     */
    @Test(dataProvider = "printfData")
    public void printfWorksCorrectly(final Locale locale, final String pattern,
        final String ret, final Object[] params)
    {
        final String key = "key";
        when(source.getKey(key)).thenReturn(pattern);

        final MessageBundle bundle = builder.appendSource(locale, source)
            .freeze();

        assertEquals(bundle.printf(locale, "key", params), ret);
    }

    @DataProvider
    public Iterator<Object[]> msgFormatIllegal()
    {
        /*
         * Examples lifted straight off the javadoc. I am not interested in
         * neanderthal technology to research further.
         */
        return Arrays.asList(
            new Object[] { "ab {0'}' de" },
            new Object[] { "ab } de" },
            new Object[] { "''{''" }
        ).iterator();
    }

    @Test(dataProvider = "msgFormatIllegal")
    public void illegalMessageFormatArgumentsAppearAsIs(final String format)
    {
        when(source.getKey("key")).thenReturn(format);
        final MessageBundle bundle = builder.appendSource(source).freeze();

        assertEquals(bundle.format("key"), format, "Hello world");
    }

    @DataProvider
    public Iterator<Object[]> msgFormatData()
    {
        final List<Object[]> list = new ArrayList<Object[]>();

        String format;
        Object[] arguments;
        String expected;

        /*
         * We need to build a date for the first Javadoc example... And we don't
         * have Joda Time, so...
         *
         * For the first example, the time had to be updated.
         */
        final Calendar calendar = Calendar.getInstance(Locale.ROOT);
        calendar.set(2053, Calendar.JULY, 3, 12, 30, 0);

        format =  "At {1,time} on {1,date}, there was {2} on planet " +
            "{0,number,integer}.";
        arguments = new Object[] { 7, calendar.getTime(),
            "a disturbance in the Force" };
        expected = "At 12:30:00 PM on Jul 3, 2053, there was a disturbance in" +
            " the Force on planet 7.";
        list.add(new Object[] { format, arguments, expected });

        format = "The disk \"{1}\" contains {0} file(s).";
        arguments = new Object[] { 1273L, "MyDisk" };
        expected = "The disk \"MyDisk\" contains 1,273 file(s).";
        list.add(new Object[] { format, arguments, expected });

        return list.iterator();
    }

    @Test(dataProvider = "msgFormatData")
    public void messageFormatWorks(final String format, final Object[] args,
        final String expected)
    {
        when(source.getKey("key")).thenReturn(format);
        final MessageBundle bundle = builder.appendSource(source).freeze();
        final String actual = bundle.format(Locale.ROOT, "key", args);

        assertEquals(actual, expected);
    }

    @Test
    public void formatRespectsLocale()
    {
        final double testDouble = 0.125;
        final String key = "key";
        final String format = "{0}";

        when(source.getKey(key)).thenReturn(format);

        final MessageBundle bundle = builder.appendSource(source).freeze();

        // Test root and French locales, number format differs
        assertEquals("0.125", bundle.format(Locale.ROOT, key, testDouble));
        assertEquals("0,125", bundle.format(Locale.FRANCE, key, testDouble));
    }

    @Test
    public void defaultLocaleIsUsedByGetMessage()
    {
        builder.appendProvider(provider).freeze().getMessage("foo");
        verify(provider).getMessageSource(Locale.getDefault());
    }

    @Test
    public void defaultLocaleIsUsedByPrintf()
    {
        builder.appendProvider(provider).freeze().printf("foo");
        verify(provider).getMessageSource(Locale.getDefault());
    }

    @Test
    public void checkNotNullBarfsOnNullButNotNonNull()
    {
        final Locale locale = Locale.GERMANY;
        final String key = "key";
        final String value = "hello";

        when(source.getKey(key)).thenReturn(value);
        when(provider.getMessageSource(locale)).thenReturn(source);

        final MessageBundle bundle = builder.appendProvider(provider).freeze();
        final String msg = bundle.getMessage(locale, key);

        try {
            bundle.checkNotNull(null, locale, key);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), msg);
        }

        bundle.checkNotNull(new Object(), locale, key);
        assertTrue(true);
    }

    @Test
    public void checkNotNullPrintfBarfsOnNullButNotNonNull()
    {
        final Locale locale = Locale.GERMANY;
        final String key = "key";
        final String format = "hello %s";
        final String arg = "world";

        when(source.getKey(key)).thenReturn(format);
        when(provider.getMessageSource(locale)).thenReturn(source);

        final MessageBundle bundle = builder.appendProvider(provider).freeze();
        final String msg = bundle.printf(locale, key, arg);

        try {
            bundle.checkNotNullPrintf(null, locale, key, arg);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), msg);
        }

        bundle.checkNotNullPrintf(new Object(), locale, key, arg);
        assertTrue(true);
    }

    @Test
    public void checkArgumentBarfsOnFalseButNotOnTrue()
    {
        final Locale locale = Locale.GERMANY;
        final String key = "key";
        final String value = "hello";

        when(source.getKey(key)).thenReturn(value);
        when(provider.getMessageSource(locale)).thenReturn(source);

        final MessageBundle bundle = builder.appendProvider(provider).freeze();
        final String msg = bundle.getMessage(locale, key);

        try {
            bundle.checkArgument(false, locale, key);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), msg);
        }

        bundle.checkArgument(true, locale, key);
        assertTrue(true);
    }

    @Test
    public void checkArgumentPrintfBarfsOnNullButNotNonNull()
    {
        final Locale locale = Locale.GERMANY;
        final String key = "key";
        final String format = "hello %s";
        final String arg = "world";

        when(source.getKey(key)).thenReturn(format);
        when(provider.getMessageSource(locale)).thenReturn(source);

        final MessageBundle bundle = builder.appendProvider(provider).freeze();
        final String msg = bundle.printf(locale, key, arg);

        try {
            bundle.checkArgumentPrintf(false, locale, key, arg);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), msg);
        }

        bundle.checkArgumentPrintf(true, locale, key, arg);
        assertTrue(true);
    }
}
