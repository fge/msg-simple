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


/**
 * Interface for one message source
 *
 * <p>A message source is simply a key/value repository.</p>
 */
public interface MessageSource
{
    /**
     * Return a message matching a given key
     *
     * <p>Note that this method MUST return {@code null} if there is no match
     * for the given key.</p>
     *
     * <p>Note also that it is guaranteed that you will never get a null key.
     * </p>
     *
     * @param key the key
     * @return see description
     */
    String getKey(final String key);
}
