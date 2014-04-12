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