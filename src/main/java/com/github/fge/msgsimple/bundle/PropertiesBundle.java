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

import com.github.fge.msgsimple.InternalBundle;
import com.github.fge.msgsimple.provider.LoadingMessageSourceProvider;
import com.github.fge.msgsimple.provider.MessageSourceLoader;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.source.PropertiesMessageSource;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Utility class to instantiate message bundles from Java property files
 *
 * <p>This is the class you will use if you want to load a legay {@link
 * ResourceBundle} (using {@link #legacyResourceBundle(String)}). However, if
 * you are starting a new bundle, it is recommended to use the other methods.
 * </p>
 *
 * <p>The generic method is {@link #forPath(String, Charset, long, TimeUnit)}.
 * You can therefore specify the character set and timeout. When no character
 * set is specified, the default is UTF-8, except for {@link
 * #legacyResourceBundle(String)} which will read property files as {@link
 * ResourceBundle} does: in ISO-8859-1.</p>
 *
 * <p>All constructors have a {@code resourcePath} argument; in the same vein
 * as {@link ResourceBundle}, the following inputs are allowed:</p>
 *
 * <ul>
 *     <li>{@code org/foobar/message.properties};</li>
 *     <li>{@code org/foobar/message};</li>
 *     <li>{@code /org/foobar/message.properties};</li>
 *     <li>{@code /org/foobar/message}.</li>
 * </ul>
 *
 * @see MessageBundle
 * @see PropertiesMessageSource
 * @see MessageSourceLoader
 * @see LoadingMessageSourceProvider
 */
public final class PropertiesBundle
{
    private static final InternalBundle BUNDLE = InternalBundle.getInstance();

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final Charset ISO = Charset.forName("ISO-8859-1");

    private static final Pattern SUFFIX = Pattern.compile("\\.properties$");

    private PropertiesBundle()
    {
    }

    /**
     * Create a message bundle from a set of property files, using the UTF-8
     * character set
     *
     * @param resourcePath the resource path
     * @throws NullPointerException resource path is null
     * @return a {@link MessageBundle}
     */
    public static MessageBundle forPath(final String resourcePath)
    {
        return createBundle(resourcePath, UTF8, 0L, null);
    }

    /**
     * Create a message bundle from a set of property files, using the UTF-8
     * character set, and an expiry delay
     *
     * @since 0.5
     *
     * @param resourcePath the resource path
     * @param duration expiry duration
     * @param timeUnit expiry time unit
     * @throws NullPointerException resource path or duration is null
     * @throws IllegalArgumentException duration is 0 or less
     * @return a {@link MessageBundle}
     *
     * @see LoadingMessageSourceProvider
     */
    public static MessageBundle forPath(final String resourcePath,
        final long duration, final TimeUnit timeUnit)
    {
        return createBundle(resourcePath, UTF8, duration, timeUnit);
    }

    /**
     * Create a message bundle from a set of property files, with a defined
     * charset and expiry time
     *
     * @since 0.5
     *
     * @param resourcePath the resource path
     * @param charset the character set
     * @param duration expiry duration
     * @param unit expiry time unit
     * @throws NullPointerException resource path, charset or duration is null
     * @throws IllegalArgumentException duration is 0 or less
     * @return a {@link MessageBundle}
     *
     * @see LoadingMessageSourceProvider
     */
    public static MessageBundle forPath(final String resourcePath,
        final Charset charset, final long duration, final TimeUnit unit)
    {
        return createBundle(resourcePath, charset, duration, unit);
    }

    /**
     * Create a message bundle mimicking a {@link ResourceBundle}
     *
     * <p>Using this method will provide a {@link MessageBundle} with the
     * following characteristics:</p>
     *
     * <ul>
     *     <li>property files will be read using the ISO-8859-1 encoding,</li>
     *     <li>no expiry time defined.</li>
     * </ul>
     *
     * <p>This method is only there for legacy reasons. Ultimately, you should
     * choose to use a more modern (ie, UTF-8) message bundle instead.</p>
     *
     * @param resourcePath the resource path
     * @return the matching bundle
     *
     * @see PropertiesMessageSource#fromResource(String, Charset)
     */
    public static MessageBundle legacyResourceBundle(final String resourcePath)
    {
        return createBundle(resourcePath, ISO, 0L, null);
    }

    // Note: "unit" nullable only if "duration" is 0L
    private static MessageBundle createBundle(final String resourcePath,
        final Charset charset, final long duration,
        @Nullable final TimeUnit unit)
    {
        BUNDLE.checkNotNull(resourcePath, "cfg.nullResourcePath");
        BUNDLE.checkNotNull(charset, "cfg.nullCharset");

        /*
         * Calculate the real path of the resource
         */
        final String s = resourcePath.startsWith("/") ? resourcePath
            : '/' + resourcePath;

        final String realPath = SUFFIX.matcher(s).replaceFirst("");

        /*
         * Create the loader implementation
         */
        final MessageSourceLoader loader = new MessageSourceLoader()
        {
            @Override
            public MessageSource load(final Locale locale)
                throws IOException
            {
                final StringBuilder sb = new StringBuilder(realPath);
                if (!locale.equals(Locale.ROOT))
                    sb.append('_').append(locale.toString());
                sb.append(".properties");

                return PropertiesMessageSource
                    .fromResource(sb.toString(), charset);
            }
        };

        /*
         * Create the MessageSourceProvider
         */
        final LoadingMessageSourceProvider.Builder builder
            = LoadingMessageSourceProvider.newBuilder().setLoader(loader);

        if (duration == 0L)
            builder.neverExpires();
        else
            builder.setLoadTimeout(duration, unit);

        final MessageSourceProvider provider = builder.build();

        return MessageBundle.newBuilder().appendProvider(provider).freeze();
    }
}
