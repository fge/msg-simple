package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.source.PropertiesMessageSource;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * A UTF-8 equivalent of a JDK's {@link ResourceBundle}
 *
 * <p>This mimics what a {@link ResourceBundle} does, with the fundamental
 * difference that the encoding used to read property files is UTF-8, and not
 * ISO-8859-1.</p>
 *
 * <p>Locale property file lookup is cached.</p>
 *
 * @see CachedMessageBundle
 * @see PropertiesMessageSource#fromResource(String)
 */
public final class PropertiesMessageBundle
    extends CachedMessageBundle
{
    private static final Pattern SUFFIX = Pattern.compile("\\.properties$");

    private final String prefix;

    /**
     * Constructor
     *
     * <p>Like {@link ResourceBundle#getBundle(String)}, you can omit both the
     * initial {@code /} and the {@code .properties} suffix in the base path.
     * </p>
     *
     * <p>Note that it is required that there be at least a matching property
     * file for {@link Locale#ROOT}.</p>
     *
     * @param basePath the base path
     * @throws NullPointerException base path is null
     * @throws IllegalArgumentException no property file found for root locale
     */
    public PropertiesMessageBundle(final String basePath)
    {
        if (basePath == null)
            throw new NullPointerException("base path must not be null");

        final String realPath = basePath.startsWith("/") ? basePath
            : '/' + basePath;

        prefix = SUFFIX.matcher(realPath).replaceFirst("");

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
