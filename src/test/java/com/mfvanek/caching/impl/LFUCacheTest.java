/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.enums.CacheType;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
class LFUCacheTest extends BaseLFUCacheTest {

    @Override
    protected Cache<String, Movie> createCache() {
        return createCache(0.1f);
    }

    @Override
    protected Cache<String, Movie> createCache(final int maxSize) {
        return createCache(maxSize, 0.1f);
    }

    @Override
    protected Cache<String, Movie> createCache(final float evictionFactor) {
        return createCache(MAX_SIZE, evictionFactor);
    }

    private static Cache<String, Movie> createCache(final int maxSize, final float evictionFactor) {
        return CacheBuilder.builder(Movie.class)
                .setCacheType(CacheType.LFU)
                .setMaxSize(maxSize)
                .setEvictionFactor(evictionFactor)
                .build();
    }
}
