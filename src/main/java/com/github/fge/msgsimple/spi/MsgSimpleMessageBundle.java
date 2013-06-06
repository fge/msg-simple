package com.github.fge.msgsimple.spi;

import com.github.fge.msgsimple.bundle.PropertiesBundle;

public final class MsgSimpleMessageBundle
    extends SimpleMessageBundleProvider
{
    public MsgSimpleMessageBundle()
    {
        put("com.github.fge:msg-simple",
            PropertiesBundle.forPath("/com/github/fge/msgsimple/messages"));
    }
}
