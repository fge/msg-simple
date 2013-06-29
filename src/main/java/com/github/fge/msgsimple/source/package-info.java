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

/**
 * Message sources
 *
 * <p>Message sources are the most low level component of the API. They are,
 * in essence, maps with key/value pairs of strings.</p>
 *
 * <p>Implementations of {@link com.github.fge.msgsimple.source.MessageSource}
 * should return {@code null} if no message is found for a given key.</p>
 *
 * <p>Two implementations are provided: one using a simple {@link java.util.Map}
 * as a backend, and another one for reading property files, either from the
 * classpath or from files on the filesystem.</p>
 */
package com.github.fge.msgsimple.source;