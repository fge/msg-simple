package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class I18NMessageBundleTest
{
    private static final Locale FR = LocaleUtils.parseLocale("fr");
    private static final Locale EN_US = LocaleUtils.parseLocale("en_US");

    private static final String KEY = "key";
    private static final String DEFAULT_MESSAGE = "default";
    private static final String FR_MESSAGE = "français !";
    private static final String EN_US_MESSAGE = "English here";

    private MessageSource rootSource;
    private MessageSource frSource;
    private MessageSource enUsSource;

    private I18NMessageBundle bundle;

    @BeforeMethod
    public void init()
    {
        bundle = mock(I18NMessageBundle.class);
        when(bundle.getSources(any(Locale.class)))
            .thenReturn(Collections.<MessageSource>emptyList());

        rootSource = mock(MessageSource.class);
        when(rootSource.getKey(KEY)).thenReturn(DEFAULT_MESSAGE);
        when(bundle.getSources(Locale.ROOT))
            .thenReturn(Arrays.asList(rootSource));

        frSource = mock(MessageSource.class);
        when(frSource.getKey(KEY)).thenReturn(FR_MESSAGE);
        when(bundle.getSources(FR))
            .thenReturn(Arrays.asList(frSource));

        enUsSource = mock(MessageSource.class);
        when(enUsSource.getKey(KEY)).thenReturn(EN_US_MESSAGE);
        when(bundle.getSources(EN_US))
            .thenReturn(Arrays.asList(enUsSource));
    }

    @DataProvider
    public Iterator<Object[]> singleKeyLookupData()
    {
        final List<Object[]> list = new ArrayList<Object[]>();

        Locale locale;
        String message;

        locale = LocaleUtils.parseLocale("fr_CA");
        message = FR_MESSAGE;
        list.add(new Object[] { locale, message });

        locale = FR;
        message = FR_MESSAGE;
        list.add(new Object[] { locale, message });

        locale = EN_US;
        message = EN_US_MESSAGE;
        list.add(new Object[] { locale, message });

        locale = LocaleUtils.parseLocale("en");
        message = DEFAULT_MESSAGE;
        list.add(new Object[] { locale, message });

        locale = LocaleUtils.parseLocale("ja_JP_JP");
        message = DEFAULT_MESSAGE;
        list.add(new Object[] { locale, message });

        locale = Locale.ROOT;
        message = DEFAULT_MESSAGE;
        list.add(new Object[] { locale, message });

        return list.iterator();
    }

    @Test(dataProvider = "singleKeyLookupData")
    public void singleKeyLookupWorksOK(final Locale locale,
        final String message)
    {
        assertEquals(bundle.getKey(KEY, locale), message);
    }

    @Test
    public void whenKeyIsNotFoundInMatchingLocaleFurtherSourcesAreTried()
    {
        final String key2 = "key2";
        final String value2 = "value2";
        when(rootSource.getKey(key2)).thenReturn(value2);

        /*
         * We have a source for en_US, but it does not have the key
         */
        assertEquals(bundle.getKey(key2, EN_US), value2);
    }

    @Test
    public void whenNoSourceMatchesKeyItselfIsReturned()
    {
        final String key3 = "key3";

        assertEquals(bundle.getKey(key3, Locale.ROOT), key3);
        assertEquals(bundle.getKey(key3, FR), key3);
        assertEquals(bundle.getKey(key3, EN_US), key3);
    }
}