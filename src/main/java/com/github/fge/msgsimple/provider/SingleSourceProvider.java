package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;

import java.util.Locale;

public final class SingleSourceProvider
    implements MessageSourceProvider
{
    private final Locale supportedLocale;
    private final MessageSource source;

    public SingleSourceProvider(final Locale supportedLocale,
        final MessageSource source)
    {
        this.supportedLocale = supportedLocale;
        this.source = source;
    }

    @Override
    public MessageSource getMessageSource(final Locale locale)
    {
        return locale.equals(supportedLocale) ? source : null;
    }
}
