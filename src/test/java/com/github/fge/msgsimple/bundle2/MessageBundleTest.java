package com.github.fge.msgsimple.bundle2;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class MessageBundleTest
{
    private MessageBundleBuilder builder;

    private MessageSourceProvider provider;
    private MessageSourceProvider provider2;

    private MessageSource source;
    private MessageSource source2;

    @BeforeMethod
    public void init()
    {
        builder = MessageBundle.newBuilder();

        provider = mock(MessageSourceProvider.class);
        provider2 = mock(MessageSourceProvider.class);

        source = mock(MessageSource.class);
        source2 = mock(MessageSource.class);
    }

    @Test
    public void cannotAppendNullProvider()
    {
        try {
            builder.appendProvider(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "cannot append null provider");
        }
    }

    @Test(dependsOnMethods = "cannotAppendNullProvider")
    public void appendedProvidersAreActuallyUsed()
    {
        final MessageBundle bundle = builder.appendProvider(provider).freeze();
        bundle.getMessage("foo", Locale.ROOT);
        verify(provider, only()).getMessageSource(Locale.ROOT);
    }

    @Test
    public void cannotPrependNullProvider()
    {
        try {
            builder.prependProvider(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "cannot prepend null provider");
        }
    }

    @Test(dependsOnMethods = {
        "cannotPrependNullProvider",
        "appendedProvidersAreActuallyUsed"
    })
    public void prependedProvidersAreUsedFirst()
    {
        final MessageBundle bundle = builder.appendProvider(provider)
            .prependProvider(provider2).freeze();

        bundle.getMessage("foo", Locale.ROOT);

        final InOrder inOrder = inOrder(provider, provider2);

        inOrder.verify(provider2).getMessageSource(Locale.ROOT);
        inOrder.verify(provider).getMessageSource(Locale.ROOT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = "prependedProvidersAreUsedFirst")
    public void localesAreAllTriedUntilRootLocale()
    {
        final MessageBundle bundle = builder.appendProvider(provider).freeze();

        final Locale locale = LocaleUtils.parseLocale("ja_JP_JP");

        final InOrder inOrder = inOrder(provider);

        bundle.getMessage("foo", locale);

        for (final Locale l: LocaleUtils.getApplicable(locale))
            inOrder.verify(provider).getMessageSource(l);

        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = "localesAreAllTriedUntilRootLocale")
    public void sourceIsQueriedForKeyWhenFound()
    {
        final Locale locale1 = LocaleUtils.parseLocale("ja_JP_JP");
        final Locale locale2 = LocaleUtils.parseLocale("ja_JP");
        final Locale locale3 = LocaleUtils.parseLocale("ja");

        final String key = "key";

        when(provider.getMessageSource(locale1)).thenReturn(source);
        when(provider.getMessageSource(locale3)).thenReturn(source2);

        final MessageBundle bundle = builder.appendProvider(provider).freeze();

        bundle.getMessage(key, locale1);

        final InOrder inOrder = inOrder(provider, source, source2);

        inOrder.verify(provider).getMessageSource(locale1);
        inOrder.verify(source).getKey(key);
        inOrder.verify(provider).getMessageSource(locale2);
        inOrder.verify(provider).getMessageSource(locale3);
        inOrder.verify(source2).getKey(key);
        inOrder.verify(provider).getMessageSource(Locale.ROOT);
        inOrder.verifyNoMoreInteractions();
    }
}
