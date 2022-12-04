/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
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
    void usingDefaultDirectory() throws Exception {
        final Cache<String, Movie> cache = createCacheDefaultDirectory();
        final Countable<String> countable = asCountable(cache);
        List<Movie> evictedItems = cache.put(SNOWDEN);
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(INCEPTION);
        assertEquals(0, evictedItems.size());
        assertEquals(2, cache.size());
        assertEquals(0, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.INCEPTION_IMDB));

        Movie value = cache.get(Movies.SNOWDEN_IMDB);
        assertEquals(SNOWDEN, value);
        assertEquals(1, countable.frequencyOf(Movies.SNOWDEN_IMDB));
        assertEquals(0, countable.frequencyOf(Movies.INCEPTION_IMDB));
    }

    @Override
    protected Cache<String, Movie> createCache() throws Exception {
        return createCache(0.1f);
    }

    @Override
    protected Cache<String, Movie> createCache(int maxSize) throws Exception {
        return createCache(maxSize, 0.1f, true);
    }

    @Override
    protected Cache<String, Movie> createCache(float evictionFactor) throws Exception {
        return createCache(MAX_SIZE, evictionFactor, true);
    }

    private static Cache<String, Movie> createCache(int maxSize, float evictionFactor, boolean useTmpDir) {
        final CacheBuilder<String, Movie> builder = CacheBuilder.getInstance(Movie.class);
        builder.setCacheType(CacheType.PERSISTENCE_LFU).setMaxSize(maxSize).setEvictionFactor(evictionFactor);
        if (useTmpDir) {
            builder.setBaseDirectory(tempDir);
        }
        return builder.build();
    }

    private static Cache<String, Movie> createCacheDefaultDirectory() throws Exception {
        return createCache(MAX_SIZE, 0.1f, false);
    }
}
