package com.github.fge.msgsimple.bundle;

import com.github.fge.msgsimple.source.MessageSource;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

// Hopefully, this class is...
@ThreadSafe
public abstract class CachedI18NMessageBundle
    extends I18NMessageBundle
{
    /**
     * Set of locales known to have failed lookup.
     *
     * <p>When a locale is in this set, it will not attempt to be reloaded.</p>
     */
    private final Set<Locale> lookupFailures
        = new CopyOnWriteArraySet<Locale>();

    /**
     * Set of message sources successfully looked up
     *
     * <p>When a source is in there, it is there permanently for now.</p>
     */
    private final ConcurrentMap<Locale, MessageSource> sources
        = new ConcurrentHashMap<Locale, MessageSource>();

    @Override
    protected final List<MessageSource> getSources(final Locale locale)
    {
        MessageSource source = sources.get(locale);

        /*
         * If found, return it
         */
        if (source != null)
            return Arrays.asList(source);

        /*
         * If it is a registered failure, return the empty list
         */
        if (lookupFailures.contains(locale))
            return Collections.emptyList();

        /*
         * OK, try and look it up. On success, register it in the sources map.
         * On failure, record the failure an return the empty list.
         */
        try {
            source = tryAndLookup(locale);
            sources.putIfAbsent(locale, source);
            return Arrays.asList(source);
        } catch (IOException ignored) {
            lookupFailures.add(locale);
            return Collections.emptyList();
        }
    }

    protected abstract MessageSource tryAndLookup(final Locale locale)
        throws IOException;

    @Override
    public final Builder modify()
    {
        throw new IllegalStateException("cached bundles cannot be modified");
    }
}
