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
 * Message source provider interface and implementations
 *
 * <p>A {@link com.github.fge.msgsimple.provider.MessageSourceProvider}
 * associates {@link com.github.fge.msgsimple.source.MessageSource}s with
 * locales.</p>
 *
 * <p>Two implementations are provided: one with static mappings, another doing
 * on demand loading. The latter requires that you provide an implementation of
 * {@link com.github.fge.msgsimple.provider.MessageSourceLoader}. You can
 * customize the expiration delay (or no expiration at all) and the load
 * timeout. The default values are respectively 10 minutes and 5 seconds.</p>
 */
package com.github.fge.msgsimple.provider;