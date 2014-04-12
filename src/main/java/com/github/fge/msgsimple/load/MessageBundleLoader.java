/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.msgsimple.load;

import com.github.fge.msgsimple.bundle.MessageBundle;

/**
 * Interface for a {@link MessageBundle} provider
 *
 * <p>Note that implementations must have a no-arg constructor.</p>
 *
 * @see MessageBundles
 */
public interface MessageBundleLoader
{
    /**
     * Get a message bundle
     *
     * @return the generated bundle
     */
    MessageBundle getBundle();
}
