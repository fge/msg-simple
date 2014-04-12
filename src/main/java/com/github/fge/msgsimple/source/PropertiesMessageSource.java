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

package com.github.fge.msgsimple.source;

import com.github.fge.msgsimple.InternalBundle;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A message source built from a properties files
 *
 * <p>You can load property files from the classpath or from files on your
 * filesystem. You can also specify the character set used to read the file;
 * methods without a character set as an argument use UTF-8.</p>
 *
 * @see Properties#load(Reader)
 */
public final class PropertiesMessageSource
    implements MessageSource
{
    private static final InternalBundle BUNDLE
        = InternalBundle.getInstance();

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Map<String, String> messages = new HashMap<String, String>();

    /**
     * Create a message source from a classpath resource using UTF-8
     *
     * @param resourcePath the path to the properties file
     * @return a newly created source
     * @throws NullPointerException resource path is null
     * @throws IOException no such resource, or an I/O error occurred while
     * reading the file
     */
    public static MessageSource fromResource(final String resourcePath)
        throws IOException
    {
        return fromResource(resourcePath, UTF8);
    }

    /**
     * Create a message source from a classpath resource using the specified
     * charset
     *
     * @since 0.5
     *
     * @param resourcePath the path to the properties file
     * @param charset the character set to use
     * @return a newly created source
     * @throws NullPointerException resource path is null
     * @throws IOException no such resource, or an I/O error occurred while
     * reading the file
     */
    public static MessageSource fromResource(final String resourcePath,
        final Charset charset)
        throws IOException
    {
        BUNDLE.checkNotNull(resourcePath, "cfg.nullResourcePath");

        final URL url = PropertiesMessageSource.class.getResource(resourcePath);

        if (url == null)
            throw new IOException(
                BUNDLE.printf("properties.resource.notFound", resourcePath));

        final InputStream in = url.openStream();
        try {
            return fromInputStream(in, charset);
        } finally {
            closeQuietly(in);
        }
    }

    /**
     * Create a message source from a properties file on the filesystem using
     * UTF-8
     *
     * @param file the file to read from
     * @return a newly created message source
     * @throws NullPointerException file is null
     * @throws FileNotFoundException file does not exist, or cannot access file
     * @throws IOException failed to read from file
     */
    public static MessageSource fromFile(final File file)
        throws IOException
    {
        return fromFile(file, UTF8);
    }

    /**
     * Create a message source from a properties file on the filesystem using
     * the specified encoding
     *
     * <p>This essentially calls {@link #fromFile(File)}.</p>
     *
     * @since 0.5
     *
     * @param path the file path
     * @return a newly created message source
     * @throws NullPointerException path is null
     * @throws IOException see {@link #fromFile(File)}
     */
    public static MessageSource fromPath(final String path)
        throws IOException
    {
        BUNDLE.checkNotNull(path, "cfg.nullPath");
        return fromFile(new File(path), UTF8);
    }

    /**
     * Create a message source from a properties file on the filesystem using
     * the specified charset
     *
     * @since 0.5
     *
     * @param file the file to read from
     * @param charset the character set to use
     * @return a newly created message source
     * @throws NullPointerException file is null
     * @throws FileNotFoundException file does not exist, or cannot access file
     * @throws IOException failed to read from file
     */
    public static MessageSource fromFile(final File file, final Charset charset)
        throws IOException
    {
        BUNDLE.checkNotNull(file, "cfg.nullFile");

        final FileInputStream in = new FileInputStream(file);

        try {
            return fromInputStream(in, charset);
        } finally {
            closeQuietly(in);
        }
    }

    /**
     * Create a message source from a properties file on the filesystem using
     * the specified charset
     *
     * <p>This essentially calls {@link #fromFile(File, Charset)}.</p>
     *
     * @since 0.5
     *
     * @param path the file path
     * @param charset the character set
     * @return a newly created message source
     * @throws NullPointerException path is null
     * @throws IOException see {@link #fromFile(File)}
     */
    public static MessageSource fromPath(final String path,
        final Charset charset)
        throws IOException
    {
        BUNDLE.checkNotNull(path, "cfg.nullPath");
        return fromFile(new File(path), charset);
    }

    /*
     * The method by which every static factory method of this class passes to
     * load the actual properties file.
     *
     * It is the caller's responsibility to close the InputStream as an
     * argument.
     */
    private static MessageSource fromInputStream(final InputStream in,
        final Charset charset)
        throws IOException
    {
        // can it ever happen?
        BUNDLE.checkNotNull(in, "cfg.nullInputStream");

        final Reader reader = new InputStreamReader(in, charset);
        try {
            final Properties properties = new Properties();
            // This method is available only since 1.6+
            properties.load(reader);
            return new PropertiesMessageSource(properties);
        } finally {
            closeQuietly(reader);
        }
    }

    private PropertiesMessageSource(final Properties properties)
    {
        // I have yet to see an example for which the full keyset of a
        // Properties object is not returned by keySet(), but...
        for (final String key: properties.stringPropertyNames())
            messages.put(key, properties.getProperty(key));
    }

    @Override
    public String getKey(final String key)
    {
        return messages.get(key);
    }

    private static void closeQuietly(final Closeable closeable)
    {
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}
