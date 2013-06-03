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

package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.util.Locale.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class FixedMessageBundleTest
{
    private FixedMessageBundle.Builder builder;
    private MessageSource source1;
    private MessageSource source2;

    @BeforeMethod
    public void init()
    {
        builder = FixedMessageBundle.newBuilder();
        source1 = mock(MessageSource.class);
        source2 = mock(MessageSource.class);
    }

    @Test
    public void cannotAppendToNullLocale()
    {
        try {
            builder.appendSource(null, source1);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "locale is null");
        }
    }

    @Test
    public void cannotPrependToNullLocale()
    {
        try {
            builder.prependSource(null, source1);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "locale is null");
        }
    }

    @Test
    public void cannotAppendNullSource()
    {
        try {
            builder.appendSource(Locale.ROOT, null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "message source is null");
        }
    }

    @Test
    public void cannotPrependNullSource()
    {
        try {
            builder.prependSource(Locale.ROOT, null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "message source is null");
        }
    }
    @Test
    public void emptyListIsReturnedForLocalesWithoutSources()
    {
        final List<MessageSource> list = builder.build().getSources(ROOT);

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test(dependsOnMethods = "emptyListIsReturnedForLocalesWithoutSources")
    public void appendedSourceIsVisible()
    {
        final MessageBundle bundle = builder.appendSource(ROOT, source1)
            .build();

        assertEquals(bundle.getSources(ROOT), Arrays.asList(source1));
    }

    @Test(dependsOnMethods = "emptyListIsReturnedForLocalesWithoutSources")
    public void prependedSourceIsVisible()
    {
        final MessageBundle bundle = builder.prependSource(ROOT, source1)
            .build();

        assertEquals(bundle.getSources(ROOT), Arrays.asList(source1));
    }

    @Test(dependsOnMethods = "appendedSourceIsVisible")
    public void multipleAppendedSourcesAreVisibleInOrder()
    {
        final MessageBundle bundle = builder.appendSource(ROOT, source1)
            .appendSource(ROOT, source2).build();

        assertEquals(bundle.getSources(ROOT), Arrays.asList(source1, source2));
    }

    @Test(dependsOnMethods = {
        "appendedSourceIsVisible",
        "prependedSourceIsVisible"
    })
    public void prependedSourcesAreVisibleBeforeAppendedSources()
    {
        final MessageBundle bundle = builder.appendSource(ROOT, source1)
            .prependSource(ROOT, source2).build();

        assertEquals(bundle.getSources(ROOT), Arrays.asList(source2, source1));
    }

    @Test(dependsOnMethods = "multipleAppendedSourcesAreVisibleInOrder")
    public void modifyingABundleWorksAsExpected()
    {
        final MessageBundle orig = builder.appendSource(ROOT, source1)
            .build();

        final MessageBundle bundle = FixedMessageBundle.modify(orig)
            .appendSource(ROOT, source2).build();

        assertEquals(bundle.getSources(ROOT), Arrays.asList(source1, source2));
    }
}
