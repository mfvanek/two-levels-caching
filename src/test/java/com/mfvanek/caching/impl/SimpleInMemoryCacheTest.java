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

import static org.assertj.core.api.Assertions.assertThat;

class SimpleInMemoryCacheTest extends BaseCacheTest {

    @Test
    @Override
    void putOnlyValue() {
        final Cache<String, Movie> cache = createCache();

        assertThat(cache.put(SNOWDEN))
                .isEmpty();
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .isTrue();
        assertThat(cache.put(AQUAMAN))
                .isEmpty();
        assertThat(cache.put(INCEPTION))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(2);
    }

    @Override
    protected Cache<String, Movie> createCache() {
        return createCache(MAX_SIZE);
    }

    @Override
    protected Cache<String, Movie> createCache(final int maxSize) {
        return CacheBuilder.builder(Movie.class)
                .setMaxSize(maxSize)
                .build();
    }
}
