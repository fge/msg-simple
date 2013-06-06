package com.github.fge.msgsimple.serviceloader;

import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Centralized access point for bundles using the {@link ServiceLoader} API
 *
 * <p>In order to register your bundle, you need two things:</p>
 *
 * <ul>
 *     <li>an implementation, in your project, of the {@link
 *     MessageBundleProvider} interface;</li>
 *     <li>a file in your classpath, by the name {@code
 *     META-INF/services/com.github.fge.msgsimple.serviceloader.MessageBundleProvider}.</li>
 * </ul>
 *
 * <p>There is a Maven plugin to help you generate the latter: see <a
 * href="https://github.com/francisdb/serviceloader-maven-plugin">here.</a> It
 * is very simple to use and it Just Works(tm).</p>
 *
 * <p>Say your {@link MessageBundleProvider} implementation is called {@code
 * MyMessageBundle} and is in package {@code com.example.util}, then, in your
 * code, this is as simple as:</p>
 *
 * <pre>
 *     import com.example.util.MyMessageBundle;
 *
 *     // In your class:
 *     private static final MessageBundle BUNDLE
 *         = MessageBundles.forClass(MyMessageBundle.class);
 * </pre>
 *
 * <p>This will automatically load the bundle for you.</p>
 */
public final class MessageBundles
{
    /*
     * FIXME: this sucks, but this is the only solution I have found to make
     * this class work reliably. Static initialization does not work, and more
     * annoyingly, neither does an enum... This is due to the fact that this
     * library eats its own dog's food to the fullest (in classes used to
     * generate bundles!).
     *
     * OK, it is not super clean (this is the very first time I use a
     * "synchronized" method!!), but it works.
     */
    private static MessageBundles INSTANCE;

    private final Map<Class<? extends MessageBundleProvider>, MessageBundle>
        bundles = new IdentityHashMap<Class<? extends MessageBundleProvider>, MessageBundle>();

    private MessageBundles()
    {
        final ServiceLoader<MessageBundleProvider> serviceLoader
            = ServiceLoader.load(MessageBundleProvider.class);
        for (final MessageBundleProvider provider: serviceLoader)
            bundles.put(provider.getClass(), provider.getBundle());
    }

    /**
     * Get a message bundle for a registered {@link MessageBundleProvider}
     * implementation
     *
     * @param c the class of the implementation
     * @return the matching bundle
     * @see ServiceLoader
     */
    public static synchronized MessageBundle forClass(
        final Class<? extends MessageBundleProvider> c)
    {
        // No choice... Bah, it's a one shot. And it's simple.
        if (INSTANCE == null)
            INSTANCE = new MessageBundles();
        return INSTANCE.bundles.get(c);
    }
}
