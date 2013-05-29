package com.github.fge.msgsimple.source;

import java.util.HashMap;
import java.util.Map;

public final class MapMessageSource
    implements MessageSource
{
    private final Map<String, String> messages;

    public MapMessageSource(final Map<String, String> messages)
    {
        checkMap(messages);
        this.messages = new HashMap<String, String>(messages);
    }

    @Override
    public String getMessage(final String key)
    {
        return messages.get(key);
    }

    private static void checkMap(final Map<String, String> map)
    {
        for (final Map.Entry<String, String> entry: map.entrySet()) {
            if (entry.getKey() == null)
                throw new NullPointerException("null keys not allowed in map");
            if (entry.getValue() == null)
                throw new NullPointerException("null values not allowed in map");
        }
    }
}
