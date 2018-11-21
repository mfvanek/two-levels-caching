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
        final Cache<String, Movie> cache = createCache();
        cache.put(SNOWDEN.getIdentifier(), SNOWDEN);

        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
    }

    @Test
    void get() {
        final Cache<String, Movie> cache = createCache(3);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);
        cache.put(INCEPTION);

        assertNull(cache.get(null));
        assertEquals(INCEPTION, cache.get(Movies.INCEPTION_IMDB));
    }

    @Test
    void containsKey() {
        final Cache<String, Movie> cache = createCache(3);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);
        cache.put(INCEPTION);

        assertFalse(cache.containsKey(null));
        assertFalse(cache.containsKey(""));
        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
    }

    @Test
    void remove() {
        // TODO
    }

    @Test
    void clear() {
        // TODO
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
        final Cache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);

        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
    }

    private static Cache<String, Movie> createCache() {
        return createCache(MAX_SIZE);
    }

    private static Cache<String, Movie> createCache(int maxSize) {
        final CacheBuilder<String, Movie> builder = CacheBuilder.getInstance();
        return builder.setMaxSize(maxSize).build();
    }
}