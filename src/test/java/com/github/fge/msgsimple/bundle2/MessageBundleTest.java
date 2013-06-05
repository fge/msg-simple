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
    public void prependedProvidersAreUsed()
    {
        final MessageBundle bundle = builder.appendProvider(provider)
            .prependProvider(provider2).freeze();

        bundle.getMessage("foo", Locale.ROOT);

        verify(provider2, only()).getMessageSource(Locale.ROOT);
    }

    @Test(dependsOnMethods = "prependedProvidersAreUsed")
    public void prependedProvidersAreUsedBeforeAppendedProviders()
    {
        final String key = "key";
        final String value = "value";
        final Locale locale = LocaleUtils.parseLocale("foo");

        when(source.getKey(key)).thenReturn(value);
        when(provider.getMessageSource(locale)).thenReturn(source);

        when(source2.getKey(key)).thenReturn(value);
        when(provider2.getMessageSource(locale)).thenReturn(source2);

        final MessageBundle bundle = builder.appendProvider(provider)
            .prependProvider(provider2).freeze();

        bundle.getMessage(key, locale);

        verify(provider2, only()).getMessageSource(locale);
        verify(source2, only()).getKey(key);

        verifyZeroInteractions(provider, source);
    }

    @Test(dependsOnMethods = "prependedProvidersAreUsedBeforeAppendedProviders")
    public void whenProviderHasNoSourceNextProviderIsTried()
    {
        final MessageBundle bundle = builder.appendProvider(provider)
            .prependProvider(provider2).freeze();

        bundle.getMessage("foo", Locale.ROOT);

        final InOrder inOrder = inOrder(provider2, provider);
        inOrder.verify(provider2).getMessageSource(Locale.ROOT);
        inOrder.verify(provider).getMessageSource(Locale.ROOT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = "whenProviderHasNoSourceNextProviderIsTried")
    public void whenSourceHasNoKeyNextProviderIsTried()
    {
        final String key = "key";
        final String value = "value";

        when(source2.getKey(key)).thenReturn(value);
        when(provider2.getMessageSource(Locale.ROOT)).thenReturn(source2);

        final MessageBundle bundle = builder.appendProvider(provider)
            .prependProvider(provider2).freeze();

        bundle.getMessage("foo", Locale.ROOT);

        final InOrder inOrder = inOrder(provider2, source2, provider);
        inOrder.verify(provider2).getMessageSource(Locale.ROOT);
        inOrder.verify(source2).getKey("foo");
        inOrder.verify(provider).getMessageSource(Locale.ROOT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = "prependedProvidersAreUsedBeforeAppendedProviders")
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
}
