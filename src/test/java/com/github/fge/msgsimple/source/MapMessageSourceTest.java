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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public final class MapMessageSourceTest
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

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
