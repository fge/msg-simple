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

package com.github.fge.msgsimple.serviceloader;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Centralized access point for bundles using the {@link ServiceLoader} API
 *
 * <p>In order to register your bundle, you need two things:</p>
 *
 * <ul>
 *     <li>an implementation, in your project, of the {@link
 *     MessageBundleProvider} interface;</li>
 *     <li>a file in your classpath, by the name {@code
 *     META-INF/services/com.github.fge.msgsimple.serviceloader.MessageBundleProvider}.</li>
 * </ul>
 *
 * <p>There is a Maven plugin to help you generate the latter: see <a
 * href="https://github.com/francisdb/serviceloader-maven-plugin">here.</a> It
 * is very simple to use and it Just Works(tm).</p>
 *
 * <p>Say your {@link MessageBundleProvider} implementation is called {@code
 * MyMessageBundle} and is in package {@code com.example.util}, then, in your
 * code, this is as simple as:</p>
 *
 * <pre>
 *     import com.example.util.MyMessageBundle;
 *
 *     // In your class:
 *     private static final MessageBundle BUNDLE
 *         = MessageBundleFactory.getBundle(MyMessageBundle.class);
 * </pre>
 *
 * <p>This will automatically load the bundle for you.</p>
 *
 * @deprecated use {@link MessageBundles} instead. Will disappear in 1.0.
 */
@Deprecated
public final class MessageBundleFactory
{
    private static final MessageBundleFactory INSTANCE;

    static {
        INSTANCE = new MessageBundleFactory();
    }
    private final Map<Class<? extends MessageBundleProvider>, MessageBundle>
        bundles = new IdentityHashMap<Class<? extends MessageBundleProvider>, MessageBundle>();

    private MessageBundleFactory()
    {
        final ServiceLoader<MessageBundleProvider> serviceLoader
            = ServiceLoader.load(MessageBundleProvider.class);
        for (final MessageBundleProvider provider: serviceLoader)
            bundles.put(provider.getClass(), provider.getBundle());
    }

    /**
     * Get a message bundle for a registered {@link MessageBundleProvider}
     * implementation
     *
     * @param c the class of the implementation
     * @return the matching bundle
     * @see ServiceLoader
     */
    public static MessageBundle getBundle(
        final Class<? extends MessageBundleProvider> c)
    {
        return INSTANCE.bundles.get(c);
    }
}
