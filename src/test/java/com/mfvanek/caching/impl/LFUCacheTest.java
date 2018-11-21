/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.enums.CacheType;
import com.mfvanek.caching.exceptions.InvalidCacheTypeException;
import com.mfvanek.caching.helpers.BaseCacheTest;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LFUCacheTest extends BaseCacheTest {

    @Test
    void put() throws Exception {
        final Cache<String, Movie> cache = createCache(2, 1.0f);
        final LFUCache<String, Movie> lfuCache = (LFUCache<String, Movie>) cache;

        List<Movie> evictedItems = cache.put(SNOWDEN);
        assertEquals(1, cache.size());
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(AQUAMAN);
        assertEquals(2, cache.size());
        assertEquals(0, evictedItems.size());
        assertEquals(0, lfuCache.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, lfuCache.frequencyOf(Movies.AQUAMAN_IMDB));

        evictedItems = cache.put(INCEPTION);
        assertEquals(1, cache.size());
        assertEquals(2, evictedItems.size());
        assertIterableEquals(Arrays.asList(SNOWDEN, AQUAMAN), evictedItems);
        assertTrue(cache.containsKey(Movies.INCEPTION_IMDB));
        assertEquals(0, lfuCache.frequencyOf(Movies.INCEPTION_IMDB));
    }

    @Test
    void putTheSameValue() throws Exception {
        final Cache<String, Movie> cache = createCache(2, 1.0f);
        final LFUCache<String, Movie> lfuCache = (LFUCache<String, Movie>) cache;
        List<Movie> evictedItems = cache.put(SNOWDEN);
        assertEquals(1, cache.size());
        assertEquals(0, evictedItems.size());
        assertEquals(0, lfuCache.frequencyOf(Movies.SNOWDEN_IMDB));

        evictedItems = cache.put(SNOWDEN);
        assertEquals(1, cache.size());
        assertEquals(0, evictedItems.size());
        assertEquals(0, lfuCache.frequencyOf(Movies.SNOWDEN_IMDB));
    }

    @Test
    void get() throws Exception {
        final Cache<String, Movie> cache = createCache();
        final LFUCache<String, Movie> lfuCache = (LFUCache<String, Movie>) cache;
        cache.put(SNOWDEN);
        cache.put(INCEPTION);
        assertEquals(2, cache.size());
        assertEquals(0, lfuCache.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, lfuCache.frequencyOf(Movies.INCEPTION_IMDB));

        Movie value = cache.get(Movies.SNOWDEN_IMDB);
        assertEquals(SNOWDEN, value);
        assertEquals(1, lfuCache.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, lfuCache.frequencyOf(Movies.INCEPTION_IMDB));

        value = cache.get(Movies.INCEPTION_IMDB);
        assertEquals(INCEPTION, value);
        assertEquals(1, lfuCache.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(1, lfuCache.frequencyOf(Movies.INCEPTION_IMDB));

        cache.get(Movies.SNOWDEN_IMDB);
        assertEquals(2, lfuCache.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(1, lfuCache.frequencyOf(Movies.INCEPTION_IMDB));

        List<Movie> evictedItems = cache.put(AQUAMAN);
        assertEquals(2, cache.size());
        assertEquals(2, lfuCache.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, lfuCache.frequencyOf(Movies.AQUAMAN_IMDB));
        assertEquals(1, evictedItems.size());
        assertEquals(INCEPTION, evictedItems.get(0));
    }

    @Test
    void remove() throws Exception {
        // TODO
    }

    @Test
    void clear() throws Exception {
        final Cache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);
        assertEquals(1, cache.size());

        Movie deletedItem = cache.remove("");
        assertNull(deletedItem);
        assertEquals(1, cache.size());

        deletedItem = cache.remove(Movies.SNOWDEN_IMDB);
        assertEquals(SNOWDEN, deletedItem);
        assertEquals(0, cache.size());
    }

    private static Cache<String, Movie> createCache() throws InvalidCacheTypeException {
        return createCache(MAX_SIZE, 0.1f);
    }

    private static Cache<String, Movie> createCache(int maxSize, float evictionFactor) throws InvalidCacheTypeException {
        final CacheBuilder<String, Movie> builder = CacheBuilder.getInstance();
        return builder.setCacheType(CacheType.LFU).setMaxSize(maxSize).setEvictionFactor(evictionFactor).build();
    }
}