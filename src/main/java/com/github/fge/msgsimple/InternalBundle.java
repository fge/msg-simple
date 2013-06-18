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

package com.github.fge.msgsimple;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Class meant for internal use by the API. Don't use!
 */
public final class InternalBundle
{
    private static final InternalBundle INSTANCE
        = new InternalBundle();

    private final Map<String, String> messages
        = new HashMap<String, String>();

    private InternalBundle()
    {
        messages.put("query.nullKey", "cannot query null keys");
        messages.put("query.nullLocale", "cannot query null locale");
        messages.put("cfg.nullProvider",
            "cannot append null message source provider");
        messages.put("cfg.nullResourcePath", "resource path cannot be null");
        messages.put("cfg.noLoader", "no loader has been provided");
        messages.put("cfg.nullLoader", "loader cannot be null");
        messages.put("cfg.nullDefaultSource",
            "when provided, the default message source must not be null");
        messages.put("cfg.nonPositiveDuration", "timeout must be greater than 0");
        messages.put("cfg.nullTimeUnit", "time unit must not be null");
        messages.put("cfg.nullKey", "null keys are not allowed");
        messages.put("cfg.nullSource", "null sources are not allowed");
        messages.put("cfg.nullMap", "null map is not allowed");
        messages.put("cfg.nullFile", "file cannot be null");
        messages.put("cfg.nullPath", "file path cannot be null");
        messages.put("cfg.nullInputStream", "provided InputStream is null");
        messages.put("cfg.map.nullKey", "null keys not allowed in map");
        messages.put("cfg.map.nullValue", "null values not allowed in map");
        messages.put("properties.resource.notFound",
            "resource \"%s\" not found");
    }

    public static InternalBundle getInstance()
    {
        return INSTANCE;
    }

    public String getMessage(final String key)
    {
        return messages.get(key);
    }

    public String printf(final String key, final Object... params)
    {
        return new Formatter().format(getMessage(key), params).toString();
    }

    public <T> T checkNotNull(final T reference, final String key)
    {
        if (reference == null)
            throw new NullPointerException(messages.get(key));
        return reference;
    }

    public void checkArgument(final boolean condition, final String key)
    {
        if (!condition)
            throw new IllegalArgumentException(messages.get(key));
    }
}
