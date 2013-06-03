package com.github.fge.msgsimple.source;


/**
 * Interface for one message source
 *
 * <p>A message source is simply a key/value repository.</p>
 */
public interface MessageSource
{
    /**
     * Return a message matching a given key
     *
     * <p>Note that this method MUST return {@code null} if there is no match
     * for the given key.</p>
     *
     * <p>Note also that it is guaranteed that you will never get a null key.
     * </p>
     *
     * @param key the key
     * @return see description
     */
    String getKey(final String key);
}
