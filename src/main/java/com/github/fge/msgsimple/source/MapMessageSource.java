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
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

    private final Map<String, String> messages;

    @Deprecated
    public MapMessageSource(final Map<String, String> messages)
    {
        this.messages = new HashMap<String, String>(checkMap(messages));
    }

    private MapMessageSource(final Builder builder)
    {
        messages = new HashMap<String, String>(builder.messages);
    }
    public static Builder newBuilder()
    {
        return new Builder();
    }

    @Override
    public String getKey(final String key)
    {
        return messages.get(key);
    }

    public static final class Builder
    {
        private final Map<String, String> messages
            = new HashMap<String, String>();

        private Builder()
        {
        }

        public Builder put(final String key, final String message)
        {
            messages.put(
                BUNDLE.checkNotNull(key, "cfg.map.nullKey"),
                BUNDLE.checkNotNull(message, "cfg.map.nullValue")
            );
            return this;
        }

        public Builder putAll(final Map<String, String> map)
        {
            messages.putAll(checkMap(map));
            return this;
        }

        public MessageSource build()
        {
            return new MapMessageSource(this);
        }
    }

    private static Map<String, String> checkMap(final Map<String, String> map)
    {
        BUNDLE.checkNotNull(map, "cfg.nullMap");

        for (final Map.Entry<String, String> entry: map.entrySet()) {
            BUNDLE.checkNotNull(entry.getKey(), "cfg.map.nullKey");
            BUNDLE.checkNotNull(entry.getValue(), "cfg.map.nullValue");
        }

        return map;
    }
}
