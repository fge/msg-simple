package com.github.fge.msgsimple.source;

import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class PropertiesMessageSourceTest
{
    private static final String KEY = "mouton";
    private static final String VALUE = "bêêêê";

    @Test
    public void propertyFilesAreReadAsUTF8()
        throws IOException
    {
        final MessageSource source
            = PropertiesMessageSource.fromResource("/t.properties");

        assertEquals(source.getMessage(KEY), VALUE);
    }

    @Test
    public void cannotLoadFromNullResourcePath()
        throws IOException
    {
        try {
            PropertiesMessageSource.fromResource(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "resource path is null");
        }
    }

    @Test
    public void loadingFromNonExistingResourcePathThrowsIOException()
    {
        try {
            PropertiesMessageSource.fromResource("foo");
            fail("No exception thrown!");
        } catch (IOException e) {
            assertEquals(e.getMessage(), "resource \"foo\" not found");
        }
    }

    @Test
    public void cannotLoadFromNullFileObject()
        throws IOException
    {
        try {
            PropertiesMessageSource.fromFile(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "file is null");
        }
    }

    @Test
    public void cannotLoadFromNullPath()
        throws IOException
    {
        try {
            PropertiesMessageSource.fromPath(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "file path is null");
        }
    }
}
