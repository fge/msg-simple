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

package com.github.fge.msgsimple.load;

import com.github.fge.msgsimple.InternalBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Centralized access point for bundles
 *
 * <p>In order to register your bundle, you simply need to have an
 * implementation of {@link MessageBundleProvider}. The first time you call this
 * factory's {@link #getBundle(Class)} with the class of this implementation,
 * it will create a cached instance of this provider and return the bundle.</p>
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
 *         = MessageBundles.getBundle(MyMessageBundle.class);
 * </pre>
 *
 * <p>This will automatically load the bundle for you.</p>
 */
public final class MessageBundles
{
    private static final InternalBundle BUNDLE = InternalBundle.getInstance();

    private static final Map<Class<? extends MessageBundleProvider>, MessageBundle>
        BUNDLES = new IdentityHashMap<Class<? extends MessageBundleProvider>, MessageBundle>();

    private MessageBundles()
    {
    }

    /**
     * Get a message bundle for a registered {@link MessageBundleProvider}
     * implementation
     *
     * @param c the class of the implementation
     * @return the matching bundle
     */
    public static synchronized MessageBundle getBundle(
        final Class<? extends MessageBundleProvider> c)
    {
        MessageBundle ret = BUNDLES.get(c);
        if (ret == null) {
            ret = doGetBundle(c);
            BUNDLES.put(c, ret);
        }
        return ret;
    }

    private static MessageBundle doGetBundle(
        final Class<? extends MessageBundleProvider> c)
    {
        final Constructor<? extends MessageBundleProvider> constructor;
        final MessageBundleProvider provider;

        String message;

        message = BUNDLE.getMessage("factory.noConstructor");
        try {
            constructor = c.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(message, e);
        }

        message = BUNDLE.getMessage("factory.cannotInstantiate");
        try {
            provider = constructor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(message, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(message, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(message, e);
        }

        return BUNDLE.checkNotNull(provider.getBundle(),
            "factory.illegalProvider");
    }
}
