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

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Map}-based message source
 *
 * <p>This is quite a simple source. All you have to provide is a {@link Map}
 * with message keys as keys and messages as values.</p>
 *
 * <p>Note that null keys or values are not allowed.</p>
 */
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
    public String getKey(final String key)
    {
        return messages.get(key);
    }

    private static void checkMap(final Map<String, String> map)
    {
        if (map == null)
            throw new NullPointerException("null map is not allowed");

        for (final Map.Entry<String, String> entry: map.entrySet()) {
            if (entry.getKey() == null)
                throw new NullPointerException("null keys not allowed in map");
            if (entry.getValue() == null)
                throw new NullPointerException("null values not allowed in map");
        }
    }
}
