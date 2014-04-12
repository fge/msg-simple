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

package com.github.fge.msgsimple.locale;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.testng.Assert.*;

public final class LocaleUtilsTest
{
    @Test
    public void parsingNullInputIsNotAllowed()
    {
        try {
            LocaleUtils.parseLocale(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "input cannot be null");
        }
    }

    @Test
    public void parsingInputWithTooManyElementsIsNotAllowed()
    {
        try {
            LocaleUtils.parseLocale("a_b_c_d");
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "malformed input a_b_c_d");
        }
    }

    @DataProvider
    public Iterator<Object[]> parsingData()
    {
        final List<Object[]> list = new ArrayList<Object[]>();

        String input, language, country, variant;

        input = "ja_JP_JP";
        language = "ja";
        country = "JP";
        variant = "JP";
        list.add(new Object[] { input, language, country, variant });

        input = "fr_FR";
        language = "fr";
        country = "FR";
        variant = "";
        list.add(new Object[] { input, language, country, variant });

        input = "it";
        language = "it";
        country = "";
        variant = "";
        list.add(new Object[] { input, language, country, variant });

        input = "foo__bar";
        language = "foo";
        country = "";
        variant = "bar";
        list.add(new Object[] { input, language, country, variant });

        input = language = country = variant = "";
        list.add(new Object[] { input, language, country, variant });

        return list.iterator();
    }

    @Test(dataProvider = "parsingData")
    public void localeParsingWorksCorrectly(final String input,
        final String language, final String country, final String variant)
    {
        final Locale locale = LocaleUtils.parseLocale(input);

        assertEquals(locale.getLanguage(), language);
        assertEquals(locale.getCountry(), country);
        assertEquals(locale.getVariant(), variant);
    }

    @DataProvider
    public Iterator<Object[]> parsedAsLocaleROOT()
    {
        return Arrays.asList(
            new Object[] { "_a" },
            new Object[] { "_" },
            new Object[] { "_a_"},
            new Object[] { "__b" },
            new Object[] { "_a_b" },
            new Object[] { "__" }
        ).iterator();
    }

    @Test(dataProvider = "parsedAsLocaleROOT")
    public void localeStringsWithEmptyLanguageAreParsedAsLocaleROOT(
        final String input)
    {
        assertEquals(LocaleUtils.parseLocale(input), Locale.ROOT);
    }

    @DataProvider
    public Iterator<Object[]> descendingLocaleLists()
    {
        final List<Object[]> list = new ArrayList<Object[]>();

        Locale baseLocale;
        List<Locale> localeList;

        baseLocale = Locale.ROOT;
        localeList = Arrays.asList(Locale.ROOT);
        list.add(new Object[] { baseLocale, localeList });

        baseLocale = LocaleUtils.parseLocale("ja_JP_JP");
        localeList = Arrays.asList(
            baseLocale,
            LocaleUtils.parseLocale("ja_JP"),
            LocaleUtils.parseLocale("ja"),
            Locale.ROOT
        );
        list.add(new Object[] { baseLocale, localeList });

        baseLocale = LocaleUtils.parseLocale("it_IT");
        localeList = Arrays.asList(
            baseLocale,
            LocaleUtils.parseLocale("it"),
            Locale.ROOT
        );
        list.add(new Object[] { baseLocale, localeList });

        baseLocale = LocaleUtils.parseLocale("nl");
        localeList = Arrays.asList(baseLocale, Locale.ROOT);
        list.add(new Object[] { baseLocale, localeList });

        baseLocale = LocaleUtils.parseLocale("foo__bar");
        localeList = Arrays.asList(
            baseLocale,
            LocaleUtils.parseLocale("foo"),
            Locale.ROOT
        );
        list.add(new Object[] { baseLocale, localeList });

        return list.iterator();
    }

    @Test(
        dependsOnMethods = "localeParsingWorksCorrectly",
        dataProvider = "descendingLocaleLists"
    )
    public void localeListCalculationWorks(final Locale baseLocale,
        final List<Locale> localeList)
    {
        assertEquals(LocaleUtils.getApplicable(baseLocale), localeList);
    }

    @DataProvider
    public Iterator<Object[]> illegalLocales()
    {
        return Arrays.asList(
            new Object[] { "a___" },
            new Object[] { "___" }
        ).iterator();
    }

    @Test(dataProvider = "illegalLocales")
    public void illegalLocalesAreRecognizedAsSuch(final String input)
    {
        try {
            LocaleUtils.parseLocale(input);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "malformed input " + input);
        }
    }
}
