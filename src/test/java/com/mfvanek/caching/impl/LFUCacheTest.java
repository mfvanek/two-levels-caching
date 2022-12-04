/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.enums.CacheType;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;

class LFUCacheTest extends BaseLFUCacheTest {

    @Override
    protected Cache<String, Movie> createCache() throws Exception {
        return createCache(0.1f);
    }

    @Override
    protected Cache<String, Movie> createCache(int maxSize) throws Exception {
        return createCache(maxSize, 0.1f);
    }

    @Override
    protected Cache<String, Movie> createCache(float evictionFactor) throws Exception {
        return createCache(MAX_SIZE, evictionFactor);
    }

    private static Cache<String, Movie> createCache(int maxSize, float evictionFactor) throws Exception {
        final CacheBuilder<String, Movie> builder = CacheBuilder.getInstance(Movie.class);
        return builder.setCacheType(CacheType.LFU).setMaxSize(maxSize).setEvictionFactor(evictionFactor).build();
    }
}
