/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.impl;

import io.github.mfvanek.caching.builders.CacheBuilder;
import io.github.mfvanek.caching.enums.CacheType;
import io.github.mfvanek.caching.interfaces.Cache;
import io.github.mfvanek.caching.models.Movie;

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
