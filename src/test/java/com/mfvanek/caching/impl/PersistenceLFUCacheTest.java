/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.enums.CacheType;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;

import java.nio.file.Files;
import java.nio.file.Path;

class PersistenceLFUCacheTest extends BaseLFUCacheTest {

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
        final Path tempDir = Files.createTempDirectory("jcache");
        return builder.setCacheType(CacheType.PERSISTENCE_LFU).setMaxSize(maxSize).
                setEvictionFactor(evictionFactor).setBaseDirectory(tempDir).build();
    }
}