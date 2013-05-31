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
            LocaleUtils.parse(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "input cannot be null");
        }
    }

    @Test
    public void parsingInputWithTooManyElementsIsNotAllowed()
    {
        try {
            LocaleUtils.parse("a_b_c_d");
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
        final Locale locale = LocaleUtils.parse(input);

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
        assertEquals(LocaleUtils.parse(input), Locale.ROOT);
    }
}
