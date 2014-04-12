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

import com.github.fge.msgsimple.bundle.MessageBundle;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;

public final class MessageBundlesTest
{
    private static final AtomicInteger COUNT = new AtomicInteger(0);
    private static final MessageBundle BUNDLE
        = MessageBundle.newBuilder().freeze();

    @Test
    public void bundleLoadOnlyHappensOnce()
    {
        final MessageBundle bundle1
            = MessageBundles.getBundle(DummyLoader.class);
        final MessageBundle bundle2
            = MessageBundles.getBundle(DummyLoader.class);

        assertSame(bundle1, bundle2);
        assertSame(bundle1, BUNDLE);
        assertEquals(COUNT.get(), 1);
    }

    public static final class DummyLoader
        implements MessageBundleLoader
    {

        @Override
        public MessageBundle getBundle()
        {
            COUNT.incrementAndGet();
            return BUNDLE;
        }
    }
}
