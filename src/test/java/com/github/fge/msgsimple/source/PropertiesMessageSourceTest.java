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

package com.github.fge.msgsimple.source;

import com.github.fge.msgsimple.InternalBundle;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.testng.Assert.*;

public final class PropertiesMessageSourceTest
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

    private static final String KEY = "mouton";
    private static final String VALUE = "bêêêê";

    @Test
    public void propertyFilesAreReadAsUTF8ByDefault()
        throws IOException
    {
        final MessageSource source
            = PropertiesMessageSource.fromResource("/t.properties");

        assertEquals(source.getKey(KEY), VALUE);
    }

    @Test
    public void propertyFilesAreLoadedUsingTheRequiredEncoding()
        throws IOException
    {
        final MessageSource source = PropertiesMessageSource
            .fromResource("/t_iso.properties", Charset.forName("ISO-8859-1"));

        assertEquals(source.getKey(KEY), VALUE);
    }

    @Test
    public void cannotLoadFromNullResourcePath()
        throws IOException
    {
        try {
            PropertiesMessageSource.fromResource(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("cfg.nullResourcePath"));
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
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.nullFile"));
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
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.nullPath"));
        }
    }

    /*
     * Test prompted by http://stackoverflow.com/questions/17170248/enum-relating-to-a-string-array-that-was-read-from-file
     *
     * In fact, test that Properties' .stringPropertyNames() correctly swallows
     * an input separated by semicolons.
     */
    @Test
    public void PropertiesStringArrayAreReadAsStrings()
        throws IOException
    {
        final MessageSource source
            = PropertiesMessageSource.fromResource("/t.properties");

        assertEquals(source.getKey("someStringArray"), "a;b;c;");
    }
}
