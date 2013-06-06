package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.locale.LocaleUtils;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.spi.MessageBundles;
import com.github.fge.msgsimple.spi.MsgSimpleMessageBundle;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class MessageBundleTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.forClass(MsgSimpleMessageBundle.class);

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
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.nullProvider"));
        }
    }

    @Test(dependsOnMethods = "cannotAppendNullProvider")
    public void appendedProvidersAreUsed()
    {
        final MessageBundle bundle = builder.appendProvider(provider).freeze();
        bundle.getMessage(Locale.ROOT, "foo");
        verify(provider, only()).getMessageSource(Locale.ROOT);
    }

    @Test(dependsOnMethods = "appendedProvidersAreUsed")
    public void appendedProvidersAreUsedInOrderOfInsertion()
    {
        final MessageBundle bundle = builder.appendProvider(provider)
            .appendProvider(provider2).freeze();

        bundle.getMessage(Locale.ROOT, "foo");

        final InOrder inOrder = inOrder(provider, provider2);

        inOrder.verify(provider).getMessageSource(Locale.ROOT);
        inOrder.verify(provider2).getMessageSource(Locale.ROOT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void cannotPrependNullProvider()
    {
        try {
            builder.prependProvider(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("cfg.nullProvider"));
        }
    }

    @Test(dependsOnMethods = {
        "cannotPrependNullProvider",
        "appendedProvidersAreUsed"
    })
    public void prependedProvidersAreUsedFirst()
    {
        final MessageBundle bundle = builder.appendProvider(provider)
            .prependProvider(provider2).freeze();

        bundle.getMessage(Locale.ROOT, "foo");

        final InOrder inOrder = inOrder(provider, provider2);

        inOrder.verify(provider2).getMessageSource(Locale.ROOT);
        inOrder.verify(provider).getMessageSource(Locale.ROOT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = "appendedProvidersAreUsed")
    public void localesAreAllTriedUntilRootLocale()
    {
        final MessageBundle bundle = builder.appendProvider(provider).freeze();

        final Locale locale = LocaleUtils.parseLocale("ja_JP_JP");

        final InOrder inOrder = inOrder(provider);

        bundle.getMessage(locale, "foo");

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

        bundle.getMessage(locale1, key);

        final InOrder inOrder = inOrder(provider, source, source2);

        inOrder.verify(provider).getMessageSource(locale1);
        inOrder.verify(source).getKey(key);
        inOrder.verify(provider).getMessageSource(locale2);
        inOrder.verify(provider).getMessageSource(locale3);
        inOrder.verify(source2).getKey(key);
        inOrder.verify(provider).getMessageSource(Locale.ROOT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = {
        "appendedProvidersAreUsed",
        "localesAreAllTriedUntilRootLocale"
    })
    public void providersAreAllTriedForOneLocaleBeforeTryingNextOne()
    {
        final MessageBundle bundle = builder.appendProvider(provider)
            .appendProvider(provider2).freeze();

        final Locale locale = LocaleUtils.parseLocale("ja_JP_JP");

        bundle.getMessage(locale, "foo");

        final InOrder inOrder = inOrder(provider, provider2);

        for (final Locale l: LocaleUtils.getApplicable(locale)) {
            inOrder.verify(provider).getMessageSource(l);
            inOrder.verify(provider2).getMessageSource(l);
        }

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void whenNoMessageIsFoundKeyIsReturned()
    {
        assertEquals(builder.freeze().getMessage(Locale.ROOT, "foo"), "foo");
    }

    @Test(dependsOnMethods = {
        "appendedProvidersAreUsed",
        "localesAreAllTriedUntilRootLocale"
    })
    public void whenKeyIsFoundMessageIsReturned()
    {
        final Locale locale = Locale.CHINA;
        final String key = "key";
        final String value = "value";

        when(source.getKey(key)).thenReturn(value);
        when(provider.getMessageSource(locale)).thenReturn(source);

        final MessageBundle bundle = builder.appendProvider(provider).freeze();

        final String msg = bundle.getMessage(locale, key);

        assertEquals(msg, value);
    }

    @Test(dependsOnMethods = "whenKeyIsFoundMessageIsReturned")
    public void whenKeyIsFoundNoFurtherProvidersOrSourcesAreTried()
    {
        final Locale locale1 = LocaleUtils.parseLocale("fr_FR");
        final Locale locale2 = LocaleUtils.parseLocale("fr");
        final String key = "key";
        final String value = "value";

        when(provider.getMessageSource(locale1)).thenReturn(source);
        when(source2.getKey(key)).thenReturn(value);
        when(provider2.getMessageSource(locale2)).thenReturn(source2);

        final MessageBundle bundle = builder.appendProvider(provider)
            .appendProvider(provider2).freeze();

        final InOrder inOrder = inOrder(provider, source, provider2, source2);

        bundle.getMessage(locale1, key);

        inOrder.verify(provider).getMessageSource(locale1);
        inOrder.verify(source).getKey(key);
        inOrder.verify(provider2).getMessageSource(locale1);
        inOrder.verify(provider).getMessageSource(locale2);
        inOrder.verify(source2).getKey(key);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void cannotQueryNullKey()
    {
        try {
            builder.freeze().getMessage(Locale.ROOT, null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("query.nullKey"));
        }
    }

    @Test
    public void cannotQueryNullLocale()
    {
        try {
            builder.freeze().getMessage(null, "foo");
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("query.nullLocale"));
        }
    }
}
