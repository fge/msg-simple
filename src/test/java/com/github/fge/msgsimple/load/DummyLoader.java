package com.github.fge.msgsimple.load;

import com.github.fge.msgsimple.bundle.MessageBundle;

public class DummyLoader
    implements MessageBundleLoader
{
    @Override
    public MessageBundle getBundle()
    {
        return MessageBundle.newBuilder().freeze();
    }
}
