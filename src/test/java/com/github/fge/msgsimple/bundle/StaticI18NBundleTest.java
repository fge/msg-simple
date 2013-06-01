package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Locale.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class StaticI18NBundleTest
{
    private I18NMessageBundle.Builder builder;
    private MessageSource source1;
    private MessageSource source2;

    @BeforeMethod
    public void init()
    {
        builder = I18NMessageBundle.newStaticBundle();
        source1 = mock(MessageSource.class);
        source2 = mock(MessageSource.class);
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
        final I18NMessageBundle bundle = builder.appendSource(ROOT, source1)
            .build();

        assertEquals(bundle.getSources(ROOT), Arrays.asList(source1));
    }

    @Test(dependsOnMethods = "emptyListIsReturnedForLocalesWithoutSources")
    public void prependedSourceIsVisible()
    {
        final I18NMessageBundle bundle = builder.prependSource(ROOT, source1)
            .build();

        assertEquals(bundle.getSources(ROOT), Arrays.asList(source1));
    }

    @Test(dependsOnMethods = "appendedSourceIsVisible")
    public void multipleAppendedSourcesAreVisibleInOrder()
    {
        final I18NMessageBundle bundle = builder.appendSource(ROOT, source1)
            .appendSource(ROOT, source2).build();

        assertEquals(bundle.getSources(ROOT), Arrays.asList(source1, source2));
    }

    @Test(dependsOnMethods = {
        "appendedSourceIsVisible",
        "prependedSourceIsVisible"
    })
    public void prependedSourcesAreVisibleBeforeAppendedSources()
    {
        final I18NMessageBundle bundle = builder.appendSource(ROOT, source1)
            .prependSource(ROOT, source2).build();

        assertEquals(bundle.getSources(ROOT), Arrays.asList(source2, source1));
    }

    @Test(dependsOnMethods = "multipleAppendedSourcesAreVisibleInOrder")
    public void modifyingABundleWorksAsExpected()
    {
        final I18NMessageBundle orig = builder.appendSource(ROOT, source1)
            .build();

        final I18NMessageBundle bundle = orig.modify()
            .appendSource(ROOT, source2).build();

        assertEquals(bundle.getSources(ROOT), Arrays.asList(source1, source2));
    }
}
