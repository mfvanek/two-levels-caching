/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

class SimpleInMemoryCacheTest {

    private static final int MAX_SIZE = 2;
    private static final Movie SNOWDEN = Movies.getSnowden();
    private static final Movie AQUAMAN = Movies.getAquaman();
    private static final Movie INCEPTION = Movies.getInception();

    @Test
    void putWithKey() {
    }

    @Test
    void get() {
    }

    @Test
    void containsKey() {
    }

    @Test
    void remove() {
    }

    @Test
    void clear() {
    }

    @Test
    void size() {
        final Cache<String, Movie> cache = createCache();
        assertEquals(0, cache.size());

        cache.put(SNOWDEN);
        assertEquals(1, cache.size());

        cache.put(AQUAMAN);
        assertEquals(MAX_SIZE, cache.size());

        cache.put(INCEPTION);
        assertEquals(MAX_SIZE, cache.size());
    }

    @Test
    void putOnlyValue() {
    }

    private Cache<String, Movie> createCache() {
        final CacheBuilder<String, Movie> builder = CacheBuilder.getInstance();
        return builder.setMaxSize(MAX_SIZE).build();
    }
}