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
        final InputStream in
            = PropertiesMessageSource.class.getResourceAsStream(resourcePath);

        return fromInputStream(in);
    }

    public static MessageSource fromFile(final File file)
        throws IOException
    {
        return fromInputStream(new FileInputStream(file));
    }

    public static MessageSource fromPath(final String path)
        throws IOException
    {
        return fromFile(new File(path));
    }

    public static MessageSource fromInputStream(final InputStream in)
        throws IOException
    {
        final Reader reader = new InputStreamReader(in, UTF8);
        try {
            final Properties properties = new Properties();
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
