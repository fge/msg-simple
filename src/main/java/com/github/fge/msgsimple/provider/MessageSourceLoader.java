package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;

import java.io.IOException;
import java.util.Locale;

/**
 * Load a message source for a given locale
 *
 * @see LoadingMessageSourceProvider
 */
public interface MessageSourceLoader
{
    /**
     * Load a message source for a locale
     *
     * @param locale the locale (guaranteed never to be {@code null}
     * @return a message source ({@code null} if not found)
     * @throws IOException loading error
     */
    MessageSource load(final Locale locale)
        throws IOException;
}
