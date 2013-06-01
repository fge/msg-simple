package com.github.fge.msgsimple.source;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * A message source built from a properties files
 *
 * <p>This can be viewed as a "single locale equivalent" of a {@link
 * ResourceBundle}; however there are key differences:</p>
 *
 * <ul>
 *     <li>the properties file is read as UTF-8;</li>
 *     <li>the message text is <i>not</i> read as a {@link MessageFormat}.</li>
 * </ul>
 *
 * <p>The latter point means you do not have to double all single quotes.</p>
 *
 * @see Properties#load(Reader)
 */
public final class PropertiesMessageSource
    implements MessageSource
{
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Map<String, String> messages = new HashMap<String, String>();

    /**
     * Create a message source from a classpath resource
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
        if (resourcePath == null)
            throw new NullPointerException("resource path is null");

        final InputStream in
            = PropertiesMessageSource.class.getResourceAsStream(resourcePath);

        if (in == null)
            throw new IOException("resource \"" + resourcePath
                + "\" not found");

        try {
            return fromInputStream(in);
        } finally {
            closeQuietly(in);
        }
    }

    /**
     * Create a message source from a file object
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
        if (file == null)
            throw new NullPointerException("file is null");

        final FileInputStream in = new FileInputStream(file);

        try {
            return fromInputStream(in);
        } finally {
            closeQuietly(in);
        }
    }

    /**
     * Create a message source from a file path
     *
     * <p>This essentially calls {@link #fromFile(File)}.</p>
     *
     * @param path the file path
     * @return a newly created message source
     * @throws NullPointerException path is null
     * @throws IOException see {@link #fromFile(File)}
     */
    public static MessageSource fromPath(final String path)
        throws IOException
    {
        if (path == null)
            throw new NullPointerException("file path is null");

        return fromFile(new File(path));
    }

    private static MessageSource fromInputStream(final InputStream in)
        throws IOException
    {
        if (in == null)
            throw new NullPointerException("input stream is null");

        final Reader reader = new InputStreamReader(in, UTF8);
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
