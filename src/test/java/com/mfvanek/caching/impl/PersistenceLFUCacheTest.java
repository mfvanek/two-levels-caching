/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.enums.CacheType;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.Countable;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersistenceLFUCacheTest extends BaseLFUCacheTest {

    @Test
    void usingDefaultDirectory() {
        final Cache<String, Movie> cache = createCacheDefaultDirectory();
        final Countable<String> countable = asCountable(cache);
        List<Movie> evictedItems = cache.put(SNOWDEN);
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(INCEPTION);
        assertEquals(0, evictedItems.size());
        assertEquals(2, cache.size());
        assertEquals(0, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.INCEPTION_IMDB));

        final Movie value = cache.get(Movies.SNOWDEN_IMDB);
        assertEquals(SNOWDEN, value);
        assertEquals(1, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.INCEPTION_IMDB));
    }

    @Override
    protected Cache<String, Movie> createCache() {
        return createCache(0.1f);
    }

    @Override
    protected Cache<String, Movie> createCache(final int maxSize) {
        return createCache(maxSize, 0.1f, true);
    }

    @Override
    protected Cache<String, Movie> createCache(final float evictionFactor) {
        return createCache(MAX_SIZE, evictionFactor, true);
    }

    private static Cache<String, Movie> createCache(final int maxSize, final float evictionFactor, final boolean useTmpDir) {
        final CacheBuilder<String, Movie> builder = CacheBuilder.getInstance(Movie.class)
                .setCacheType(CacheType.PERSISTENCE_LFU)
                .setMaxSize(maxSize)
                .setEvictionFactor(evictionFactor);
        if (useTmpDir) {
            builder.setBaseDirectory(tempDir);
        }
        return builder.build();
    }

    private static Cache<String, Movie> createCacheDefaultDirectory() {
        return createCache(MAX_SIZE, 0.1f, false);
    }
}
