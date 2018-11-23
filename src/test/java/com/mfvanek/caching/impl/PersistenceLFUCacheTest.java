/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.enums.CacheType;
import com.mfvanek.caching.helpers.BaseCacheTest;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceLFUCacheTest extends BaseCacheTest {

    @Test
    void put() throws Exception {
        final Cache<String, Movie> cache = createCache(1.0f);
        final PersistenceLFUCache<String, Movie> lfuCache = (PersistenceLFUCache<String, Movie>) cache;

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
    }

    @Override
    protected Cache<String, Movie> createCache() throws Exception {
        return createCache(0.1f);
    }

    private static Cache<String, Movie> createCache(float evictionFactor) throws Exception {
        final CacheBuilder<String, Movie> builder = CacheBuilder.getInstance(Movie.class);
        final Path tempDir = Files.createTempDirectory("jcache");
        return builder.setCacheType(CacheType.PERSISTENCE_LFU).setMaxSize(MAX_SIZE).
                setEvictionFactor(evictionFactor).setBaseDirectory(tempDir).build();
    }
}