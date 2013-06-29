package com.github.fge.msgsimple.load;

import com.github.fge.msgsimple.bundle.MessageBundle;

public class DummyProvider
    implements MessageBundleProvider
{
    @Override
    public MessageBundle getBundle()
    {
        return MessageBundle.newBuilder().freeze();
    }
}
