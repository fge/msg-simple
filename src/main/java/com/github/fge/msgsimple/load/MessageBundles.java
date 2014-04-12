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
 * implementation of {@link MessageBundleLoader}. The first time you call this
 * factory's {@link #getBundle(Class)} with the class of this implementation,
 * it will create a cached instance of this provider and return the bundle.</p>
 *
 * <p>Say your {@link MessageBundleLoader} implementation is called {@code
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

    private static final Map<Class<? extends MessageBundleLoader>, MessageBundle>
        BUNDLES = new IdentityHashMap<Class<? extends MessageBundleLoader>, MessageBundle>();

    private MessageBundles()
    {
    }

    /**
     * Get a message bundle for a registered {@link MessageBundleLoader}
     * implementation
     *
     * @param c the class of the implementation
     * @return the matching bundle
     */
    public static synchronized MessageBundle getBundle(
        final Class<? extends MessageBundleLoader> c)
    {
        MessageBundle ret = BUNDLES.get(c);
        if (ret == null) {
            ret = doGetBundle(c);
            BUNDLES.put(c, ret);
        }
        return ret;
    }

    private static MessageBundle doGetBundle(
        final Class<? extends MessageBundleLoader> c)
    {
        final Constructor<? extends MessageBundleLoader> constructor;
        final MessageBundleLoader provider;

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
