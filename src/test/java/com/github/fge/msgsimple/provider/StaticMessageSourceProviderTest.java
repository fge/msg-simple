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

package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.InternalBundle;
import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class StaticMessageSourceProviderTest
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

    private MessageSource source;
    private MessageSource source2;

    private StaticMessageSourceProvider.Builder builder;

    @BeforeMethod
    public void init()
    {
        source = mock(MessageSource.class);
        source2 = mock(MessageSource.class);
        builder = StaticMessageSourceProvider.newBuilder();
    }

    @Test
    public void cannotSetNullDefaultSource()
    {
        try {
            builder.setDefaultSource(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("cfg.nullDefaultSource"));
        }
    }

    @Test
    public void cannotAddSourceForNullLocale()
    {
        try {
            builder.addSource(null, source);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.nullKey"));
        }
    }

    @Test
    public void cannotAddNullSourceForLocale()
    {
        try {
            builder.addSource(Locale.ROOT, null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.nullSource"));
        }
    }

    @Test
    public void defaultSourceIsProvidedIfNoOtherSourceIsPresent()
    {
        builder.setDefaultSource(source);
        assertSame(builder.build().getMessageSource(Locale.ROOT), source);
    }

    @Test(dependsOnMethods = "defaultSourceIsProvidedIfNoOtherSourceIsPresent")
    public void localeSpecificSourceOverridesDefaultSource()
    {
        final Locale locale = Locale.CHINA;
        builder.setDefaultSource(source).addSource(locale, source2);
        assertSame(builder.build().getMessageSource(locale), source2);
        assertSame(builder.build().getMessageSource(Locale.ROOT), source);
    }

    @Test(dependsOnMethods = "localeSpecificSourceOverridesDefaultSource")
    public void localeSpecificSourceIsLatestAdded()
    {
        final Locale locale = Locale.CHINA;
        builder.addSource(locale, source).addSource(locale, source2);
        assertSame(builder.build().getMessageSource(locale), source2);
    }
}
