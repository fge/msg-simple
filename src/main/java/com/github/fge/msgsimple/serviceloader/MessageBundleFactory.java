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

package com.github.fge.msgsimple.serviceloader;

import com.github.fge.msgsimple.bundle.MessageBundle;

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
 */
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
