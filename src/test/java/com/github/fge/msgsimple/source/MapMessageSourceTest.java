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

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.spi.MessageBundles;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public final class MapMessageSourceTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getByName("com.github.fge:msg-simple");

    @Test
    public void mapContentsAreCopiedCorrectly()
    {
        final String key = "foo";
        final String value = "bar";

        final Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);

        final MessageSource source = new MapMessageSource(map);

        assertEquals(source.getKey(key), value);
    }

    @Test
    public void nullMapIsNotAllowed()
    {
        try {
            new MapMessageSource(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.nullMap"));
        }
    }

    @Test
    public void nullKeysAreNotAllowedInMap()
    {
        final Map<String, String> map = new HashMap<String, String>();
        map.put(null, null);

        try {
            new MapMessageSource(map);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.map.nullKey"));
        }
    }

    @Test
    public void nullValuesAreNotAllowedInMap()
    {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("a", null);

        try {
            new MapMessageSource(map);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.map.nullValue"));
        }
    }
}
