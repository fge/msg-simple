package com.github.fge.msgsimple.locale;

import org.testng.annotations.Test;

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
}
