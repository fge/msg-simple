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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.testng.Assert.*;

public final class PropertiesBundleTest
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();
    private static final MessageBundle TEST_BUNDLE
        = PropertiesBundle.forPath("/org/foobar/msg");
    private static final String KEY = "hello";

    @Test
    public void constructorRefusesNullPrefix()
    {
        try {
            PropertiesBundle.forPath(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("cfg.nullResourcePath"));
        }
    }

    @DataProvider
    public Iterator<Object[]> lookups()
    {
        final List<Object[]> list = new ArrayList<Object[]>();

        String locale, message;

        locale = "";
        message = "world";
        list.add(new Object[] { LocaleUtils.parseLocale(locale), message });

        locale = "fr";
        message = "le monde";
        list.add(new Object[] { LocaleUtils.parseLocale(locale), message });

        locale = "it_IT";
        message = "il mondo";
        list.add(new Object[] { LocaleUtils.parseLocale(locale), message });

        locale = "fr_FR";
        message = "le monde";
        list.add(new Object[] { LocaleUtils.parseLocale(locale), message });

        locale = "es";
        message = "world";
        list.add(new Object[] { LocaleUtils.parseLocale(locale), message });

        return list.iterator();
    }

    @Test(dataProvider = "lookups")
    public void bundleLookupWorksCorrectly(final Locale locale,
        final String msg)
    {
        assertEquals(TEST_BUNDLE.getMessage(locale, KEY), msg);
    }
}
