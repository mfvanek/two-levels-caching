/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.TwoLevelsCacheBuilder;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TwoLevelsCacheTest extends BaseCacheTest {

    @Test
    @Override
    final void putOnlyValue() {
        final Cache<String, Movie> cache = createCache(1.0f);

        List<Movie> evictedItems = cache.put(SNOWDEN);
        assertEquals(1, cache.size());
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(AQUAMAN);
        assertEquals(2, cache.size());
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(INCEPTION);
        assertEquals(3, cache.size());
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(INTERSTELLAR);
        assertEquals(4, cache.size());
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(ARRIVAL);
        assertEquals(3, cache.size());
        assertEquals(2, evictedItems.size());
        assertIterableEquals(List.of(SNOWDEN, AQUAMAN), evictedItems);
        assertTrue(cache.containsKey(Movies.ARRIVAL_IMDB));
        assertTrue(cache.containsKey(Movies.INTERSTELLAR_IMDB));
    }

    @Test
    @Override
    final void size() {
        final Cache<String, Movie> cache = createCache();
        assertEquals(0, cache.size());

        cache.put(SNOWDEN);
        assertEquals(1, cache.size());

        cache.put(AQUAMAN);
        assertEquals(2, cache.size());

        cache.put(INCEPTION);
        assertEquals(3, cache.size());

        cache.put(INTERSTELLAR);
        assertEquals(4, cache.size());

        cache.put(ARRIVAL);
        assertEquals(4, cache.size());
    }

    @Test
    void containsKeyInSecondLevel() {
        final Cache<String, Movie> cache = createCache(1);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);

        assertEquals(2, cache.size());
        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB)); // level 2
        assertTrue(cache.containsKey(Movies.AQUAMAN_IMDB)); // level 1
    }

    @Override
    protected Cache<String, Movie> createCache() {
        return createCache(MAX_SIZE);
    }

    @Override
    protected Cache<String, Movie> createCache(final int maxSize) {
        return createCache(maxSize, 0.1f);
    }

    private static Cache<String, Movie> createCache(final float evictionFactor) {
        return createCache(MAX_SIZE, evictionFactor);
    }

    private static Cache<String, Movie> createCache(final int maxSize, final float evictionFactor) {
        return TwoLevelsCacheBuilder.getInstance(Movie.class)
                .setBaseDirectory(tempDir)
                .setFirstLevelMaxSize(maxSize)
                .setSecondLevelMaxSize(maxSize)
                .setFirstLevelEvictionFactor(evictionFactor)
                .setSecondLevelEvictionFactor(evictionFactor)
                .build();
    }
}
