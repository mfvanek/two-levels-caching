/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.Countable;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

abstract class BaseLFUCacheTest extends BaseCacheTest {

    private static AbstractCache<String, Movie> asAbstractCache(final Cache<String, Movie> cache) {
        if (cache instanceof AbstractCache) {
            return (AbstractCache<String, Movie>) cache;
        }
        throw new ClassCastException(cache.getClass().toString());
    }

    protected abstract Cache<String, Movie> createCache(float evictionFactor);

    @Test
    @Override
    final void putTheSameValue() {
        final Cache<String, Movie> cache = createCache(1.0f);
        assertThat(cache.put(SNOWDEN))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(1);
        assertThat(cache.frequencyOf(Movies.SNOWDEN_IMDB))
                .isZero();

        assertThat(cache.put(SNOWDEN))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(1);
        assertThat(cache.frequencyOf(Movies.SNOWDEN_IMDB))
                .isZero();
    }

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
        assertThat(cache.frequencyOf(Movies.SNOWDEN_IMDB))
                .isZero();
        assertThat(cache.frequencyOf(Movies.AQUAMAN_IMDB))
                .isZero();

        assertThat(cache.put(INCEPTION))
                .hasSize(2)
                .containsExactlyInAnyOrder(SNOWDEN, AQUAMAN);
        assertThat(cache.size())
                .isEqualTo(1);
        assertThat(cache.containsKey(Movies.INCEPTION_IMDB))
                .isTrue();
        assertThat(cache.frequencyOf(Movies.INCEPTION_IMDB))
                .isZero();
    }

    @Test
    final void evictionWithDifferentFrequencies() {
        final Cache<String, Movie> cache = createCache(1.0f);

        assertThat(cache.put(SNOWDEN))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(1);
        assertThat(cache.get(Movies.SNOWDEN_IMDB))
                .isEqualTo(SNOWDEN);

        assertThat(cache.put(AQUAMAN))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(2);

        assertThat(cache.frequencyOf(Movies.SNOWDEN_IMDB))
                .isEqualTo(1);
        assertThat(cache.frequencyOf(Movies.AQUAMAN_IMDB))
                .isZero();

        assertThat(cache.put(INTERSTELLAR))
                .hasSize(2)
                .containsExactlyInAnyOrder(SNOWDEN, AQUAMAN);
        assertThat(cache.size())
                .isEqualTo(1);
    }

    @Test
    @Override
    void get() {
        final Cache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);
        cache.put(INCEPTION);
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

        assertThat(cache.get(Movies.INCEPTION_IMDB))
                .isEqualTo(INCEPTION);
        assertThat(cache.frequencyOf(Movies.SNOWDEN_IMDB))
                .isEqualTo(1);
        assertThat(cache.frequencyOf(Movies.INCEPTION_IMDB))
                .isEqualTo(1);

        assertThat(cache.get(Movies.SNOWDEN_IMDB))
                .isEqualTo(SNOWDEN);
        assertThat(cache.frequencyOf(Movies.SNOWDEN_IMDB))
                .isEqualTo(2);
        assertThat(cache.frequencyOf(Movies.INCEPTION_IMDB))
                .isEqualTo(1);

        assertThat(cache.put(AQUAMAN))
                .hasSize(1)
                .containsExactlyInAnyOrder(INCEPTION);
        assertThat(cache.size())
                .isEqualTo(2);
        assertThat(cache.frequencyOf(Movies.SNOWDEN_IMDB))
                .isEqualTo(2);
        assertThat(cache.frequencyOf(Movies.AQUAMAN_IMDB))
                .isZero();
        assertThatThrownBy(() -> cache.frequencyOf(Movies.INCEPTION_IMDB))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Key tt1375666 not found in the cache");
    }

    @Test
    final void innerRemoveNotExisting() {
        final Cache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);
        cache.put(AQUAMAN);
        assertThat(cache.size())
                .isEqualTo(2);

        assertThat(asAbstractCache(cache).innerRemove("not existing key"))
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.getKey()).isEqualTo(Countable.INVALID_FREQUENCY);
                    assertThat(e.getValue()).isNull();
                });
        assertThat(cache.size())
                .isEqualTo(2);
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .isTrue();
        assertThat(cache.containsKey(Movies.AQUAMAN_IMDB))
                .isTrue();
    }

    @Test
    final void innerRemove() {
        final Cache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);
        cache.put(AQUAMAN);
        cache.get(Movies.SNOWDEN_IMDB);
        assertThat(cache.size())
                .isEqualTo(2);
        assertThat(cache.frequencyOf(Movies.SNOWDEN_IMDB))
                .isEqualTo(1);

        assertThat(asAbstractCache(cache).innerRemove(Movies.AQUAMAN_IMDB))
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.getKey()).isEqualTo(0);
                    assertThat(e.getValue()).isEqualTo(AQUAMAN);
                });
        assertThat(cache.size())
                .isEqualTo(1);
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .isTrue();
        assertThat(cache.containsKey(Movies.AQUAMAN_IMDB))
                .isFalse();

        assertThat(asAbstractCache(cache).innerRemove(Movies.SNOWDEN_IMDB))
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.getKey()).isEqualTo(1);
                    assertThat(e.getValue()).isEqualTo(SNOWDEN);
                });
        assertThat(cache.size())
                .isZero();
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .isFalse();
    }
}
