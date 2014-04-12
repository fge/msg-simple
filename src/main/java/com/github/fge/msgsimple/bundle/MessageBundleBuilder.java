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

package com.github.fge.msgsimple.bundle;

import com.github.fge.Thawed;
import com.github.fge.msgsimple.InternalBundle;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.provider.StaticMessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Builder class for a message bundle
 *
 * <p>You cannot instantiate this class directly: use {@link
 * MessageBundle#newBuilder()}, or {@link MessageBundle#thaw() thaw} another
 * bundle.</p>
 *
 * <p>This class is the {@link Thawed} counterpart of a {@link MessageBundle}.
 * </p>
 *
 * @see MessageBundle
 */
public final class MessageBundleBuilder
    implements Thawed<MessageBundle>
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

    final List<MessageSourceProvider> providers
        = new ArrayList<MessageSourceProvider>();

    MessageBundleBuilder()
    {
    }

    MessageBundleBuilder(final MessageBundle bundle)
    {
        providers.addAll(bundle.providers);
    }

    /**
     * Append a message provider
     *
     * @param provider the provider
     * @throws NullPointerException provider is null
     * @return this
     */
    public MessageBundleBuilder appendProvider(
        final MessageSourceProvider provider)
    {
        BUNDLE.checkNotNull(provider, "cfg.nullProvider");
        providers.add(provider);
        return this;
    }

    /**
     * Prepend a message provider
     *
     * @param provider the provider
     * @throws NullPointerException provider is null
     * @return this
     */
    public MessageBundleBuilder prependProvider(
        final MessageSourceProvider provider)
    {
        BUNDLE.checkNotNull(provider, "cfg.nullProvider");
        providers.add(0, provider);
        return this;
    }

    /**
     * Convenience method to append a single-source provider
     *
     * @param source the message source
     * @return this
     * @see StaticMessageSourceProvider#withSingleSource(MessageSource)
     */
    public MessageBundleBuilder appendSource(final MessageSource source)
    {
        final MessageSourceProvider provider
            = StaticMessageSourceProvider.withSingleSource(source);
        providers.add(provider);
        return this;
    }

    /**
     * Convenience method to prepend a single-source provider
     *
     * @param source the message source
     * @return this
     * @see StaticMessageSourceProvider#withSingleSource(MessageSource)
     */
    public MessageBundleBuilder prependSource(final MessageSource source)
    {
        final MessageSourceProvider provider
            = StaticMessageSourceProvider.withSingleSource(source);
        providers.add(0, provider);
        return this;
    }

    /**
     * Convenience method to append a single-source provider for a given locale
     *
     * @param source the message source
     * @return this
     * @see StaticMessageSourceProvider#withSingleSource(Locale, MessageSource)
     */
    public MessageBundleBuilder appendSource(final Locale locale,
        final MessageSource source)
    {
        final MessageSourceProvider provider
            = StaticMessageSourceProvider.withSingleSource(locale, source);
        providers.add(provider);
        return this;
    }

    /**
     * Convenience method to prepend a single-source provider for a given locale
     *
     * @param source the message source
     * @return this
     * @see StaticMessageSourceProvider#withSingleSource(Locale, MessageSource)
     */
    public MessageBundleBuilder prependSource(final Locale locale,
        final MessageSource source)
    {
        final MessageSourceProvider provider
            = StaticMessageSourceProvider.withSingleSource(locale, source);
        providers.add(0, provider);
        return this;
    }

    /**
     * Append all message source providers from another bundle
     *
     * @param bundle the bundle
     * @return this
     * @throws NullPointerException bundle is null
     */
    public MessageBundleBuilder appendBundle(final MessageBundle bundle)
    {
        BUNDLE.checkNotNull(bundle, "cfg.nullBundle");
        providers.addAll(bundle.providers);
        return this;
    }

    /**
     * Prepend all message source providers from another bundle
     *
     * @param bundle the bundle
     * @return this
     * @throws NullPointerException bundle is null
     */
    public MessageBundleBuilder prependBundle(final MessageBundle bundle)
    {
        BUNDLE.checkNotNull(bundle, "cfg.nullBundle");
        final List<MessageSourceProvider> list
            = new ArrayList<MessageSourceProvider>();
        list.addAll(bundle.providers);
        list.addAll(providers);
        providers.clear();
        providers.addAll(list);
        return this;
    }

    @Override
    public MessageBundle freeze()
    {
        return new MessageBundle(this);
    }
}
