package com.github.fge.msgsimple.spi;

import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.Map;

public interface MessageBundleProvider
{
    Map<String, MessageBundle> getBundles();
}
