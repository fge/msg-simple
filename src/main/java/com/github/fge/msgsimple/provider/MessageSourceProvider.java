package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;

import java.util.Locale;

/**
 * Provider for one message source, according to a given locale
 */
public interface MessageSourceProvider
{
    /**
     * Provide a message source for a given locale
     *
     * @param locale the locale
     * @return a matching message source; {@code null} if none is found
     */
    MessageSource getMessageSource(final Locale locale);
}
