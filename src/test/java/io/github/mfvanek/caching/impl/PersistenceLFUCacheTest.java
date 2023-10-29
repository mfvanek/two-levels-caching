/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.impl;

import io.github.mfvanek.caching.builders.CacheBuilder;
import io.github.mfvanek.caching.enums.CacheType;
import io.github.mfvanek.caching.interfaces.LeveledCache;
import io.github.mfvanek.caching.models.Movie;
import io.github.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceLFUCacheTest extends BaseLFUCacheTest {

    @Test
    void usingDefaultDirectory() {
        final LeveledCache<String, Movie> cache = createCacheDefaultDirectory();
        assertThat(cache.put(SNOWDEN))
                .isEmpty();

        assertThat(cache.put(INCEPTION))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(2);
        assertThat(cache.frequencyOf(Movies.SNOWDEN_IMDB))
                .isZero();
        assertThat(cache.frequencyOf(Movies.INCEPTION_IMDB))
                .isZero();

        assertThat(cache.get(Movies.SNOWDEN_IMDB))
                .isEqualTo(SNOWDEN);
        assertThat(cache.frequencyOf(Movies.SNOWDEN_IMDB))
                .isEqualTo(1);
        assertThat(cache.frequencyOf(Movies.INCEPTION_IMDB))
                .isZero();
    }

    @Override
    protected LeveledCache<String, Movie> createCache() {
        return createCache(0.1f);
    }

    @Override
    protected LeveledCache<String, Movie> createCache(final int maxSize) {
        return createCache(maxSize, 0.1f, true);
    }

    @Override
    protected LeveledCache<String, Movie> createCache(final float evictionFactor) {
        return createCache(MAX_SIZE, evictionFactor, true);
    }

    private static LeveledCache<String, Movie> createCache(final int maxSize, final float evictionFactor, final boolean useTmpDir) {
        final CacheBuilder<String, Movie> builder = CacheBuilder.builder(Movie.class)
                .setCacheType(CacheType.PERSISTENCE_LFU)
                .setMaxSize(maxSize)
                .setEvictionFactor(evictionFactor);
        if (useTmpDir) {
            builder.setBaseDirectory(tempDir);
        }
        return builder.build();
    }

    private static LeveledCache<String, Movie> createCacheDefaultDirectory() {
        return createCache(MAX_SIZE, 0.1f, false);
    }
}
