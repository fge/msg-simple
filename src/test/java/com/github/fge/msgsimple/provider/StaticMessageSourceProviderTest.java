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
