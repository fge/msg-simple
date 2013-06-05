package com.github.fge.msgsimple.provider;

import com.github.fge.msgsimple.source.MessageSource;

import java.io.IOException;
import java.util.Locale;

public interface MessageSourceLoader
{
    MessageSource load(final Locale locale)
        throws IOException;
}
