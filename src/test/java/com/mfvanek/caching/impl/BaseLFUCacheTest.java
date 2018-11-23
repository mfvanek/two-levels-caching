/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.Countable;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class BaseLFUCacheTest extends BaseCacheTest {

    private static Countable<String> asCountable(final Cache<String, Movie> cache) throws ClassCastException {
        if (cache instanceof Countable) {
            //noinspection unchecked
            return (Countable<String>) cache;
        }
        throw new ClassCastException();
    }

    protected abstract Cache<String, Movie> createCache(float evictionFactor) throws Exception;

    @Test
    @Override
    final void putTheSameValue() throws Exception {
        final Cache<String, Movie> cache = createCache(1.0f);
        final Countable<String> countable = asCountable(cache);
        List<Movie> evictedItems = cache.put(SNOWDEN);
        assertEquals(1, cache.size());
        assertEquals(0, evictedItems.size());
        assertEquals(0, countable.frequencyOf(Movies.SNOWDEN_IMDB));

        evictedItems = cache.put(SNOWDEN);
        assertEquals(1, cache.size());
        assertEquals(0, evictedItems.size());
        assertEquals(0, countable.frequencyOf(Movies.SNOWDEN_IMDB));
    }

    @Test
    @Override
    final void putOnlyValue() throws Exception {
        final Cache<String, Movie> cache = createCache(1.0f);
        final Countable<String> countable = asCountable(cache);

        List<Movie> evictedItems = cache.put(SNOWDEN);
        assertEquals(1, cache.size());
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(AQUAMAN);
        assertEquals(2, cache.size());
        assertEquals(0, evictedItems.size());
        assertEquals(0, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.AQUAMAN_IMDB));

        evictedItems = cache.put(INCEPTION);
        assertEquals(1, cache.size());
        assertEquals(2, evictedItems.size());
        assertIterableEquals(Arrays.asList(SNOWDEN, AQUAMAN), evictedItems);
        assertTrue(cache.containsKey(Movies.INCEPTION_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.INCEPTION_IMDB));
    }

    @Test
    @Override
    void get() throws Exception {
        final Cache<String, Movie> cache = createCache();
        final Countable<String> countable = asCountable(cache);
        cache.put(SNOWDEN);
        cache.put(INCEPTION);
        assertEquals(2, cache.size());
        assertEquals(0, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.INCEPTION_IMDB));

        Movie value = cache.get(Movies.SNOWDEN_IMDB);
        assertEquals(SNOWDEN, value);
        assertEquals(1, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.INCEPTION_IMDB));

        value = cache.get(Movies.INCEPTION_IMDB);
        assertEquals(INCEPTION, value);
        assertEquals(1, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(1, countable.frequencyOf(Movies.INCEPTION_IMDB));

        cache.get(Movies.SNOWDEN_IMDB);
        assertEquals(2, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(1, countable.frequencyOf(Movies.INCEPTION_IMDB));

        List<Movie> evictedItems = cache.put(AQUAMAN);
        assertEquals(2, cache.size());
        assertEquals(2, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.AQUAMAN_IMDB));
        assertEquals(1, evictedItems.size());
        assertEquals(INCEPTION, evictedItems.get(0));
    }
}
