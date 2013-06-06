package com.github.fge.msgsimple.serviceloader;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.bundle.PropertiesBundle;

/**
 * This library's own implementation of {@link MessageBundleProvider}
 */
public final class MsgSimpleMessageBundle
    implements MessageBundleProvider
{

    @Override
    public MessageBundle getBundle()
    {
        return PropertiesBundle
            .forPath("/com/github/fge/msgsimple/messages");
    }
}
