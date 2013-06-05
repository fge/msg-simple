package com.github.fge.msgsimple.bundle2;

import com.github.fge.msgsimple.provider.LoadingMessageSourceProvider;
import com.github.fge.msgsimple.provider.MessageSourceLoader;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.source.PropertiesMessageSource;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

public final class PropertiesBundle
{
    private static final Pattern SUFFIX = Pattern.compile("\\.properties$");

    private PropertiesBundle()
    {
    }

    public static MessageBundle forPath(final String resourcePath)
    {
        if (resourcePath == null)
            throw new NullPointerException("resource path is null");

        final String s = resourcePath.startsWith("/") ? resourcePath
            : '/' + resourcePath;

        final String realPath = SUFFIX.matcher(s).replaceFirst("");

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

                return PropertiesMessageSource.fromResource(sb.toString());
            }
        };

        final MessageSourceProvider provider
            = LoadingMessageSourceProvider.newBuilder().setLoader(loader)
                .build();

        return MessageBundle.newBuilder().appendProvider(provider).freeze();
    }
}
