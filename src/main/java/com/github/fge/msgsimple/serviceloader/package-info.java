/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
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