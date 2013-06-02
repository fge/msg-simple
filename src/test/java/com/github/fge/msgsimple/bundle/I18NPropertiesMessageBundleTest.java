package com.github.fge.msgsimple.bundle;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class I18NPropertiesMessageBundleTest
{
    private static final I18NMessageBundle BUNDLE
        = new I18NPropertiesMessageBundle("/org/foobar/msg");
    private static final String KEY = "hello";

    @Test
    public void constructorRefusesNullPrefix()
    {
        try {
            new I18NPropertiesMessageBundle(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "base path must not be null");
        }
    }

    @Test
    public void constructorRefusesCreationOfBundleWithoutRootLocale()
    {
        try {
            new I18NPropertiesMessageBundle("foo");
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "there must be at least" +
                " a properties file for Locale.ROOT; none was found");
        }
    }

    @DataProvider
    public Iterator<Object[]> lookups()
    {
        final List<Object[]> list = new ArrayList<Object[]>();

        String locale, message;

        locale = "";
        message = "world";
        list.add(new Object[] { locale, message });

        locale = "fr";
        message = "le monde";
        list.add(new Object[] { locale, message });

        locale = "it_IT";
        message = "il mondo";
        list.add(new Object[] { locale, message });

        locale = "fr_FR";
        message = "le monde";
        list.add(new Object[] { locale, message });

        locale = "es";
        message = "world";
        list.add(new Object[] { locale, message });

        return list.iterator();
    }

    @Test(dataProvider = "lookups", threadPoolSize = 10, invocationCount = 2)
    public void bundleLookupWorksCorrectly(final String locale,
        final String msg)
    {
        assertEquals(BUNDLE.getKey(KEY, locale), msg);
    }
}
