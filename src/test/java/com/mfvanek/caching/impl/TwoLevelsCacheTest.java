/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.TwoLevelsCacheBuilder;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TwoLevelsCacheTest extends BaseCacheTest {

    @Test
    @Override
    final void putOnlyValue() {
        final Cache<String, Movie> cache = createCache(1.0f);

        assertThat(cache.put(SNOWDEN))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(1);

        assertThat(cache.put(AQUAMAN))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(2);

        assertThat(cache.put(INCEPTION))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(3);

        assertThat(cache.put(INTERSTELLAR))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(4);

        assertThat(cache.put(ARRIVAL))
                .hasSize(2)
                .containsExactlyInAnyOrder(SNOWDEN, AQUAMAN);
        assertThat(cache.size())
                .isEqualTo(3);
        assertThat(cache.containsKey(Movies.ARRIVAL_IMDB))
                .isTrue();
        assertThat(cache.containsKey(Movies.INTERSTELLAR_IMDB))
                .isTrue();
    }

    @Test
    @Override
    final void size() {
        final Cache<String, Movie> cache = createCache();
        assertThat(cache.size())
                .isZero();

        cache.put(SNOWDEN);
        assertThat(cache.size())
                .isEqualTo(1);

        cache.put(AQUAMAN);
        assertThat(cache.size())
                .isEqualTo(2);

        cache.put(INCEPTION);
        assertThat(cache.size())
                .isEqualTo(3);

        cache.put(INTERSTELLAR);
        assertThat(cache.size())
                .isEqualTo(4);

        cache.put(ARRIVAL);
        assertThat(cache.size())
                .isEqualTo(4);
    }

    @Test
    void containsKeyInSecondLevel() {
        final Cache<String, Movie> cache = createCache(1);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);

        assertThat(cache.size())
                .isEqualTo(2);
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .as("level 2")
                .isTrue();
        assertThat(cache.containsKey(Movies.AQUAMAN_IMDB))
                .as("level 1")
                .isTrue();
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
        return TwoLevelsCacheBuilder.builder(Movie.class)
                .setBaseDirectory(tempDir)
                .setFirstLevelMaxSize(maxSize)
                .setSecondLevelMaxSize(maxSize)
                .setFirstLevelEvictionFactor(evictionFactor)
                .setSecondLevelEvictionFactor(evictionFactor)
                .build();
    }
}
