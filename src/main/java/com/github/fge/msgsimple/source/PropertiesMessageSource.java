package com.github.fge.msgsimple.source;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class PropertiesMessageSource
    implements MessageSource
{
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Map<String, String> messages = new HashMap<String, String>();

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

        return fromInputStream(in);
    }

    public static MessageSource fromFile(final File file)
        throws IOException
    {
        if (file == null)
            throw new NullPointerException("file is null");

        return fromInputStream(new FileInputStream(file));
    }

    public static MessageSource fromPath(final String path)
        throws IOException
    {
        if (path == null)
            throw new NullPointerException("file path is null");

        return fromFile(new File(path));
    }

    // NOTE: CLOSES THE INPUT STREAM!
    // Don't forget to mention in javadoc
    public static MessageSource fromInputStream(final InputStream in)
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
            closeQuietly(in);
        }
    }

    private PropertiesMessageSource(final Properties properties)
    {
        for (final String key: properties.stringPropertyNames())
            messages.put(key, properties.getProperty(key));
    }

    @Override
    public String getMessage(final String key)
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
