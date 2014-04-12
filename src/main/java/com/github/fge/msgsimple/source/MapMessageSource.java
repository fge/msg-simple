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

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Map}-based message source
 *
 * <p>This is a simple message source using a {@link Map} as a key/value pair to
 * look up messages.</p>
 *
 * <p>In order to build such a source, use {@link #newBuilder()}. Sample:</p>
 *
 * <pre>
 *     final MessageSource source = MapMessageSource.newBuilder()
 *         .put("key1", "message1").put("key2", "message2")
 *         .putAll(existingMap).build();
 * </pre>
 *
 * <p>Note that null keys or values are not allowed.</p>
 *
 * @see Builder
 */
public final class MapMessageSource
    implements MessageSource
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

    private final Map<String, String> messages;

    private MapMessageSource(final Builder builder)
    {
        messages = new HashMap<String, String>(builder.messages);
    }

    /**
     * Create a new builder for a map message source
     *
     * @return a {@link Builder}
     */
    public static Builder newBuilder()
    {
        return new Builder();
    }

    @Override
    public String getKey(final String key)
    {
        return messages.get(key);
    }

    /**
     * Builder class for a {@link MapMessageSource}
     */
    public static final class Builder
    {
        private final Map<String, String> messages
            = new HashMap<String, String>();

        private Builder()
        {
        }

        /**
         * Add one key/message pair
         *
         * <p>This overrides the value if the key already existed.</p>
         *
         * @param key the key
         * @param message the message
         * @return this
         * @throws NullPointerException either the key or the value is null
         */
        public Builder put(final String key, final String message)
        {
            BUNDLE.checkNotNull(key, "cfg.map.nullKey");
            BUNDLE.checkNotNull(message, "cfg.map.nullValue");
            messages.put(key, message);
            return this;
        }

        /**
         * Add a map of key/message pairs
         *
         * <p>This overrides all values of already existing keys.</p>
         *
         * @param map the map
         * @return this
         * @throws NullPointerException the map is null; or a key, or value, is
         * null
         */
        public Builder putAll(final Map<String, String> map)
        {
            BUNDLE.checkNotNull(map, "cfg.nullMap");
            for (final Map.Entry<String, String> entry: map.entrySet())
                put(entry.getKey(), entry.getValue());
            return this;
        }

        /**
         * Build a new message source from the contents of this builder
         *
         * @return a {@link MapMessageSource}
         */
        public MessageSource build()
        {
            return new MapMessageSource(this);
        }
    }
}
