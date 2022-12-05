/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleInMemoryCacheTest extends BaseCacheTest {

    @Test
    @Override
    void putOnlyValue() {
        // Arrange
        final Cache<String, Movie> cache = createCache();

        // Act
        List<Movie> evictedItems = cache.put(SNOWDEN);

        // Assert
        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(AQUAMAN);
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(INCEPTION);
        assertEquals(0, evictedItems.size());
    }

    @Override
    protected Cache<String, Movie> createCache() {
        return createCache(MAX_SIZE);
    }

    @Override
    protected Cache<String, Movie> createCache(final int maxSize) {
        return CacheBuilder.getInstance(Movie.class)
                .setMaxSize(maxSize)
                .build();
    }
}
