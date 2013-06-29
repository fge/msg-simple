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
 * {@link java.util.ServiceLoader} support - DEPRECATED
 *
 * <p>In order to use the service loader support, you will need to implement
 * {@link com.github.fge.msgsimple.serviceloader.MessageBundleProvider}, then
 * create a file named {@code
 * META-INF/services/com.github.fge.msgsimple.serviceloader.MessageBundleProvider}
 * in your classpath, with the fully qualified class names. For instance:</p>
 *
 * <pre>
 *     com.mycompany.bundle.MyFirstBundle
 *     com.mycompany.bundle.MySecondBundle
 * </pre>
 *
 * <p>At run time, you will then be able to retrieve your bundles using:</p>
 *
 * <pre>
 *     final MessageBundle bundle
 *         = MessageBundleFactory.getBundle(MyFirstBundle.class);
 * </pre>
 *
 * @deprecated use {@link com.github.fge.msgsimple.load} instead, which does the
 * same but does not require a dedicated file.
 */
package com.github.fge.msgsimple.serviceloader;