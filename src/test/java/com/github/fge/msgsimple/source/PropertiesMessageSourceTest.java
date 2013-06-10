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

package com.github.fge.msgsimple.source;

import com.github.fge.msgsimple.InternalBundle;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class PropertiesMessageSourceTest
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

    private static final String KEY = "mouton";
    private static final String VALUE = "bêêêê";

    @Test
    public void propertyFilesAreReadAsUTF8()
        throws IOException
    {
        final MessageSource source
            = PropertiesMessageSource.fromResource("/t.properties");

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
}
