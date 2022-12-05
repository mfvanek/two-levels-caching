/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.CacheExtended;
import com.mfvanek.caching.interfaces.Countable;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class BaseLFUCacheTest extends BaseCacheTest {

    @SuppressWarnings("unchecked")
    protected static Countable<String> asCountable(final Cache<String, Movie> cache) {
        if (cache instanceof Countable) {
            return (Countable<String>) cache;
        }
        throw new ClassCastException(cache.getClass().toString());
    }

    private static CacheExtended<String, Movie> asExtended(final Cache<String, Movie> cache)
            throws ClassCastException {
        if (cache instanceof CacheExtended) {
            return (CacheExtended<String, Movie>) cache;
        }
        throw new ClassCastException();
    }

    protected abstract Cache<String, Movie> createCache(float evictionFactor);

    @Test
    @Override
    final void putTheSameValue() {
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
    final void putOnlyValue() {
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
        assertIterableEquals(List.of(SNOWDEN, AQUAMAN), evictedItems);
        assertTrue(cache.containsKey(Movies.INCEPTION_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.INCEPTION_IMDB));
    }

    @Test
    final void evictionWithDifferentFrequencies() {
        final Cache<String, Movie> cache = createCache(1.0f);
        final Countable<String> countable = asCountable(cache);

        List<Movie> evictedItems = cache.put(SNOWDEN);
        assertEquals(1, cache.size());
        assertEquals(0, evictedItems.size());
        assertNotNull(cache.get(Movies.SNOWDEN_IMDB));

        evictedItems = cache.put(AQUAMAN);
        assertEquals(2, cache.size());
        assertEquals(0, evictedItems.size());

        assertEquals(1, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.AQUAMAN_IMDB));

        evictedItems = cache.put(INTERSTELLAR);
        assertEquals(1, cache.size());
        assertEquals(2, evictedItems.size());
        assertThat(evictedItems, containsInAnyOrder(SNOWDEN, AQUAMAN));
    }

    @Test
    @Override
    void get() {
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

        final List<Movie> evictedItems = cache.put(AQUAMAN);
        assertEquals(2, cache.size());
        assertEquals(2, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.AQUAMAN_IMDB));
        assertEquals(1, evictedItems.size());
        assertEquals(INCEPTION, evictedItems.get(0));
    }

    @Test
    final void innerRemoveNotExisting() {
        final CacheExtended<String, Movie> cache = asExtended(createCache());
        cache.put(SNOWDEN);
        cache.put(AQUAMAN);
        assertEquals(MAX_SIZE, cache.size());

        final Map.Entry<Integer, Movie> deleted = cache.innerRemove("not existing key");
        assertNotNull(deleted);
        assertEquals(CacheExtended.INVALID_FREQUENCY, deleted.getKey());
        assertNull(deleted.getValue());
        assertEquals(MAX_SIZE, cache.size());
        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
        assertTrue(cache.containsKey(Movies.AQUAMAN_IMDB));
    }

    @Test
    final void innerRemove() {
        // Arrange
        final CacheExtended<String, Movie> cache = asExtended(createCache());
        cache.put(SNOWDEN);
        cache.put(AQUAMAN);
        cache.get(Movies.SNOWDEN_IMDB);
        assertEquals(MAX_SIZE, cache.size());
        assertEquals(1, cache.frequencyOf(Movies.SNOWDEN_IMDB));

        // Act
        Map.Entry<Integer, Movie> deleted = cache.innerRemove(Movies.AQUAMAN_IMDB);
        assertNotNull(deleted);
        assertEquals(Integer.valueOf(0), deleted.getKey());
        assertEquals(AQUAMAN, deleted.getValue());
        assertEquals(1, cache.size());
        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
        assertFalse(cache.containsKey(Movies.AQUAMAN_IMDB));

        deleted = cache.innerRemove(Movies.SNOWDEN_IMDB);
        assertNotNull(deleted);
        assertEquals(Integer.valueOf(1), deleted.getKey());
        assertEquals(SNOWDEN, deleted.getValue());
        assertEquals(0, cache.size());
        assertFalse(cache.containsKey(Movies.SNOWDEN_IMDB));
    }
}
