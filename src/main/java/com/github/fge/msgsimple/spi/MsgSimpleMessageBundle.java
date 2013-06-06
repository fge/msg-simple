package com.github.fge.msgsimple.spi;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.bundle.PropertiesBundle;

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
