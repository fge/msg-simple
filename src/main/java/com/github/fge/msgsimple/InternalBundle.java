package com.github.fge.msgsimple;

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
        messages.put("cfg.nonPositiveTimeout", "timeout must be greater than 0");
        messages.put("cfg.nullTimeUnit", "time unit must not be null");
        messages.put("cfg.nullKey", "null keys are not allowed");
        messages.put("cfg.nullSource", "null sources are not allowed");
        messages.put("cfg.nullMap", "null map is not allowed");
        messages.put("cfg.map.nullKey", "null keys not allowed in map");
        messages.put("cfg.map.nullValue", "null values not allowed in map");
    }

    public static InternalBundle getInstance()
    {
        return INSTANCE;
    }

    public String getMessage(final String key)
    {
        return messages.get(key);
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
