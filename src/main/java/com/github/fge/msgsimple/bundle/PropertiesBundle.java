/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.InternalBundle;
import com.github.fge.msgsimple.provider.LoadingMessageSourceProvider;
import com.github.fge.msgsimple.provider.MessageSourceLoader;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.source.PropertiesMessageSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Formatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Utility class to build a localized, UTF-8 {@link ResourceBundle}
 *
 * <p>This class only contains one method to return a {@link MessageBundle}
 * which operates nearly the same as a {@link ResourceBundle}, with two
 * differences:</p>
 *
 * <ul>
 *     <li>property files are read in UTF-8, not ISO-8859-1;</li>
 *     <li>formatted messages use {@link Formatter}, not {@link
 *     MessageFormat}.</li>
 * </ul>
 *
 * <p>Similarly to a {@link ResourceBundle}, it will try and look up a key in
 * "locale-enabled" property files (for instance, {@code foo_fr_FR.properties}
 * for bundle {@code foo} and locale {@code fr_FR}), and "descend" to less
 * specific locales (in this case, {@code fr} then {@link Locale#ROOT}) if an
 * exact match is not found.</p>
 *
 * <p>As it is a {@link MessageBundle}, it means you can extend it further by
 * {@link MessageBundle#thaw()}ing it and appending/prepending other message
 * source providers etc.</p>
 *
 * <p><b>IMPORTANT NOTE:</b> the default behaviour is to NOT expire if a load
 * succeeds or fails, for backwards compatibility reasons. However, you can use
 * the appropriate methods if you want expiry.</p>
 *
 * @see PropertiesMessageSource
 * @see MessageSourceLoader
 * @see LoadingMessageSourceProvider
 */
public final class PropertiesBundle
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

    private static final Pattern SUFFIX = Pattern.compile("\\.properties$");

    private PropertiesBundle()
    {
    }

    /**
     * Create a {@link ResourceBundle}-like {@link MessageBundle}
     *
     * <p>The path given as an argument can be any of the following:</p>
     *
     * <ul>
     *     <li>{@code org/foobar/message.properties};</li>
     *     <li>{@code org/foobar/message};</li>
     *     <li>{@code /org/foobar/message.properties};</li>
     *     <li>{@code /org/foobar/message}.</li>
     * </ul>
     *
     * @param resourcePath the resource path
     * @throws NullPointerException resource path is null
     * @return a {@link MessageBundle}
     */
    public static MessageBundle forPath(final String resourcePath)
    {
        BUNDLE.checkNotNull(resourcePath, "cfg.nullResourcePath");

        final String s = resourcePath.startsWith("/") ? resourcePath
            : '/' + resourcePath;

        final String realPath = SUFFIX.matcher(s).replaceFirst("");

        final LoadingMessageSourceProvider.Builder builder
            = createBuilder(realPath, Charset.forName("UTF-8"));

        final MessageSourceProvider provider = builder.neverExpires().build();

        return MessageBundle.newBuilder().appendProvider(provider).freeze();
    }

    /**
     * Create a {@link ResourceBundle}-like {@link MessageBundle} with expiry
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
        BUNDLE.checkNotNull(resourcePath, "cfg.nullResourcePath");

        final String s = resourcePath.startsWith("/") ? resourcePath
            : '/' + resourcePath;

        final String realPath = SUFFIX.matcher(s).replaceFirst("");

        final LoadingMessageSourceProvider.Builder builder
            = createBuilder(realPath, Charset.forName("UTF-8"));

        final MessageSourceProvider provider = builder.setExpiryTime(duration,
            timeUnit).build();

        return MessageBundle.newBuilder().appendProvider(provider).freeze();
    }

    private static LoadingMessageSourceProvider.Builder createBuilder(
        final String resourcePath, final Charset charset)
    {
        final MessageSourceLoader loader = new MessageSourceLoader()
        {
            @Override
            public MessageSource load(final Locale locale)
                throws IOException
            {
                final StringBuilder sb = new StringBuilder(resourcePath);
                if (!locale.equals(Locale.ROOT))
                    sb.append('_').append(locale.toString());
                sb.append(".properties");

                return PropertiesMessageSource
                    .fromResource(sb.toString(), charset);
            }
        };
        return LoadingMessageSourceProvider.newBuilder().setLoader(loader);
    }
}
