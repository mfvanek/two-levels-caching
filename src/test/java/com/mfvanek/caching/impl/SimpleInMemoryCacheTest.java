/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.exceptions.InvalidCacheTypeException;
import com.mfvanek.caching.helpers.BaseCacheTest;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class SimpleInMemoryCacheTest extends BaseCacheTest {

    @Test
    void putWithKey() throws Exception {
        final Cache<String, Movie> cache = createCache();
        List<Map.Entry<String, Movie>> evictedItems = cache.put(SNOWDEN.getIdentifier(), SNOWDEN);

        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
        assertEquals(0, evictedItems.size());
    }

    @Test
    void get() throws Exception {
        final Cache<String, Movie> cache = createCache(3);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);
        cache.put(INCEPTION);

        assertNull(cache.get(null));
        assertEquals(INCEPTION, cache.get(Movies.INCEPTION_IMDB));
    }

    @Test
    void containsKey() throws Exception {
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
    void size() throws Exception {
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
    void putOnlyValue() throws Exception {
        final Cache<String, Movie> cache = createCache();
        List<Movie> evictedItems = cache.put(SNOWDEN);

        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
        assertEquals(0, evictedItems.size());
    }

    private static Cache<String, Movie> createCache() throws InvalidCacheTypeException {
        return createCache(MAX_SIZE);
    }

    private static Cache<String, Movie> createCache(int maxSize) throws InvalidCacheTypeException {
        final CacheBuilder<String, Movie> builder = CacheBuilder.getInstance();
        return builder.setMaxSize(maxSize).build();
    }
}