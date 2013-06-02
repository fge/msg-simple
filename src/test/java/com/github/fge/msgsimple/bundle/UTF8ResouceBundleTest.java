package com.github.fge.msgsimple.bundle;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class UTF8ResouceBundleTest
{
    @Test
    public void constructorRefusesNullPrefix()
    {
        try {
            new UTF8ResouceBundle(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "base path must not be null");
        }
    }

    @Test
    public void constructorRefusesCreationOfBundleWithoutRootLocale()
    {
        try {
            new UTF8ResouceBundle("foo");
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "there must be at least" +
                " a properties file for Locale.ROOT; none was found");
        }
    }
}
