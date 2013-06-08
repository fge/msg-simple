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
import com.github.fge.msgsimple.serviceloader.MessageBundles;
import com.github.fge.msgsimple.serviceloader.MsgSimpleMessageBundle;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public final class MapMessageSourceTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.forClass(MsgSimpleMessageBundle.class);

    private MapMessageSource.Builder builder;

    @BeforeMethod
    public void init()
    {
        builder = MapMessageSource.newBuilder();
    }

    @Test
    public void builderDoesNotAllowNullKeys()
    {
        try {
            builder.put(null, null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.map.nullKey"));
        }
    }

    @Test
    public void builderDoesNotAllowNullValues()
    {
        try {
            builder.put("foo", null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.map.nullValue"));
        }
    }

    @Test
    public void injectedEntriesAreVisibleInBuiltValue()
    {
        final String key = "key";
        final String value = "whatever";

        builder.put(key, value);

        assertEquals(builder.build().getKey(key), value);
    }

    @Test
    public void builderDoesNotAllowNullMap()
    {
        try {
            builder.putAll(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.nullMap"));
        }
    }

    @Test
    public void builderDoesNotAllowNullMapKeys()
    {
        final Map<String, String> map = new HashMap<String, String>();
        map.put(null, null);

        try {
            builder.putAll(map);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.map.nullKey"));
        }
    }

    @Test
    public void builderDoesNotAllowNullMapValues()
    {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("a", null);

        try {
            builder.putAll(map);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("cfg.map.nullValue"));
        }
    }

    @Test
    public void injectMapsShowUpInBuiltValue()
    {
        final String key = "key";
        final String value = "value";
        final Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);

        assertEquals(builder.putAll(map).build().getKey(key), value);
    }
}
