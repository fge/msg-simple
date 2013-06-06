package com.github.fge.msgsimple.serviceloader;

import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.ServiceLoader;

/**
 * {@link ServiceLoader} implementation for this library
 *
 * @see MessageBundles
 */
public interface MessageBundleProvider
{
    /**
     * Get a message bundle
     *
     * @return the generated bundle
     */
    MessageBundle getBundle();
}
