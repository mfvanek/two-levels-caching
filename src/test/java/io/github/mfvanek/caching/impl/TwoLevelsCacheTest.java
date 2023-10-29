/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.impl;

import io.github.mfvanek.caching.builders.TwoLevelsCacheBuilder;
import io.github.mfvanek.caching.interfaces.LeveledCache;
import io.github.mfvanek.caching.models.Movie;
import io.github.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TwoLevelsCacheTest extends BaseCacheTest {

    @Test
    @Override
    final void putOnlyValue() {
        final LeveledCache<String, Movie> cache = createCache(1.0f);

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
        final LeveledCache<String, Movie> cache = createCache();
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
        final LeveledCache<String, Movie> cache = createCache(1);
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
    protected LeveledCache<String, Movie> createCache() {
        return createCache(MAX_SIZE);
    }

    @Override
    protected LeveledCache<String, Movie> createCache(final int maxSize) {
        return createCache(maxSize, 0.1f);
    }

    private static LeveledCache<String, Movie> createCache(final float evictionFactor) {
        return createCache(MAX_SIZE, evictionFactor);
    }

    private static LeveledCache<String, Movie> createCache(final int maxSize, final float evictionFactor) {
        return TwoLevelsCacheBuilder.builder(Movie.class)
                .setBaseDirectory(tempDir)
                .setFirstLevelMaxSize(maxSize)
                .setSecondLevelMaxSize(maxSize)
                .setFirstLevelEvictionFactor(evictionFactor)
                .setSecondLevelEvictionFactor(evictionFactor)
                .build();
    }
}
