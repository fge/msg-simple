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

package com.github.fge.msgsimple.bundle2;

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
    private static final MessageBundle BUNDLE
        = PropertiesBundle.forPath("/org/foobar/msg");
    private static final String KEY = "hello";

    @Test
    public void constructorRefusesNullPrefix()
    {
        try {
            PropertiesBundle.forPath(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "resource path is null");
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
        assertEquals(BUNDLE.getMessage(locale, KEY), msg);
    }
}
