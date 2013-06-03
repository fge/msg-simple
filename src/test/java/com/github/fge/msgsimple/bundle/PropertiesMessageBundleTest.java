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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class PropertiesMessageBundleTest
{
    private static final MessageBundle BUNDLE
        = new PropertiesMessageBundle("/org/foobar/msg");
    private static final String KEY = "hello";

    @Test
    public void constructorRefusesNullPrefix()
    {
        try {
            new PropertiesMessageBundle(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "base path must not be null");
        }
    }

    @Test
    public void constructorRefusesCreationOfBundleWithoutRootLocale()
    {
        try {
            new PropertiesMessageBundle("foo");
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
        assertEquals(BUNDLE.getMessage(KEY, locale), msg);
    }
}
