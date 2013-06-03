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
