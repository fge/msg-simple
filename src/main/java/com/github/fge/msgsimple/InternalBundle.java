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

package com.github.fge.msgsimple;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Class meant for internal use by the API. Don't use!
 */
public final class InternalBundle
{
    private static final InternalBundle INSTANCE = new InternalBundle();

    private final Map<String, String> messages = new HashMap<String, String>();

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
        messages.put("properties.resource.notFound", "resource \"%s\" not found");
        messages.put("cfg.nullCharset", "charset cannot be null");
        messages.put("cfg.nullBundle", "bundle cannot be null");
        messages.put("factory.noConstructor", "bundle provider does not have" +
            " a no-arg constructor");
        messages.put("factory.cannotInstantiate", "cannot instantiate bundle" +
            " provider");
        messages.put("factory.illegalProvider", "bundle provider returns null");
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
