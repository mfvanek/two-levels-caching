/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.exceptions;

import com.mfvanek.caching.enums.CacheType;

public class InvalidCacheTypeException extends RuntimeException {

    public InvalidCacheTypeException(final CacheType cacheType) {
        super("Unsupported cache type " + cacheType);
    }
}
