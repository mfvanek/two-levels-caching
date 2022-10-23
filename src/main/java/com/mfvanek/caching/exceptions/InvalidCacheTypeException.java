/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.exceptions;

import com.mfvanek.caching.enums.CacheType;

public class InvalidCacheTypeException extends RuntimeException {

    public InvalidCacheTypeException(CacheType cacheType) {
        super("Unsupported cache type " + cacheType);
    }
}
