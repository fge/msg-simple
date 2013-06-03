package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.source.PropertiesMessageSource;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * A UTF-8 capable, {@link String}-only version of a {@link ResourceBundle}
 */
public final class PropertiesMessageBundle
    extends CachedMessageBundle
{
    private static final Pattern SUFFIX = Pattern.compile("\\.properties$");

    private final String prefix;

    // FIXME: make this constructor package local and create a static factory
    // method in MessageBundle?
    public PropertiesMessageBundle(final String basePath)
    {
        if (basePath == null)
            throw new NullPointerException("base path must not be null");

        prefix = SUFFIX.matcher(basePath).replaceFirst("");

        try {
            tryAndLookup(Locale.ROOT);
        } catch (IOException e) {
            throw new IllegalArgumentException("there must be at least" +
                " a properties file for Locale.ROOT; none was found", e);
        }
    }

    @Override
    protected MessageSource tryAndLookup(final Locale locale)
        throws IOException
    {
        final StringBuilder sb = new StringBuilder(prefix);
        final String localeString = locale.toString();

        if (!localeString.isEmpty())
            sb.append('_').append(localeString);

        sb.append(".properties");

        return PropertiesMessageSource.fromResource(sb.toString());
    }
}
