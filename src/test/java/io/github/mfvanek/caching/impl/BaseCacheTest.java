/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.impl;

import io.github.mfvanek.caching.builders.CacheBuilder;
import io.github.mfvanek.caching.helpers.DirectoryUtils;
import io.github.mfvanek.caching.interfaces.LeveledCache;
import io.github.mfvanek.caching.models.Movie;
import io.github.mfvanek.caching.models.Movies;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SuppressWarnings("PMD.MutableStaticState")
abstract class BaseCacheTest {

    protected static final int MAX_SIZE = 2;
    protected static final Movie SNOWDEN = Movies.getSnowden();
    protected static final Movie AQUAMAN = Movies.getAquaman();
    protected static final Movie INCEPTION = Movies.getInception();
    protected static final Movie INTERSTELLAR = Movies.getInterstellar();
    protected static final Movie ARRIVAL = Movies.getArrival();
    protected static Path tempDir;

    protected abstract LeveledCache<String, Movie> createCache();

    protected abstract LeveledCache<String, Movie> createCache(int maxSize);

    @SneakyThrows
    @BeforeAll
    static void setUp() {
        tempDir = Files.createTempDirectory("jcache");
    }

    @AfterAll
    static void tearDown() {
        DirectoryUtils.deleteDirectory(tempDir);
        DirectoryUtils.deleteDirectory(CacheBuilder.getDefaultBaseDirectory());
    }

    @Test
    void size() {
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
                .isEqualTo(2);
    }

    @Test
    final void clear() {
        final LeveledCache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);
        cache.put(AQUAMAN);
        assertThat(cache.size())
                .isEqualTo(2);

        cache.clear();
        assertThat(cache.size())
                .isZero();
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .isFalse();
        assertThat(cache.containsKey(Movies.AQUAMAN_IMDB))
                .isFalse();
    }

    @Test
    final void containsKey() {
        final LeveledCache<String, Movie> cache = createCache(3);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);
        cache.put(INCEPTION);

        assertThat(cache.containsKey(null))
                .isFalse();
        assertThat(cache.containsKey(""))
                .isFalse();
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .isTrue();
    }

    @Test
    final void removeNotExisting() {
        final LeveledCache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);
        cache.put(AQUAMAN);
        assertThat(cache.size())
                .isEqualTo(2);

        assertThat(cache.remove("not existing key"))
                .isNull();
        assertThat(cache.size())
                .isEqualTo(2);
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .isTrue();
        assertThat(cache.containsKey(Movies.AQUAMAN_IMDB))
                .isTrue();
    }

    @Test
    final void remove() {
        final LeveledCache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);
        cache.put(AQUAMAN);
        assertThat(cache.size())
                .isEqualTo(2);

        assertThat(cache.remove(Movies.AQUAMAN_IMDB))
                .isEqualTo(AQUAMAN);
        assertThat(cache.size())
                .isEqualTo(1);
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .isTrue();
        assertThat(cache.containsKey(Movies.AQUAMAN_IMDB))
                .isFalse();

        assertThat(cache.remove(Movies.SNOWDEN_IMDB))
                .isEqualTo(SNOWDEN);
        assertThat(cache.size())
                .isZero();
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .isFalse();
    }

    @Test
    final void putWithKey() {
        final LeveledCache<String, Movie> cache = createCache();

        assertThat(cache.put(SNOWDEN.getIdentifier(), SNOWDEN))
                .isEmpty();
        assertThat(cache.containsKey(Movies.SNOWDEN_IMDB))
                .isTrue();
    }

    @Test
    void putTheSameValue() {
        final LeveledCache<String, Movie> cache = createCache();
        assertThat(cache.put(SNOWDEN))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(1);

        // put the same value again
        assertThat(cache.put(SNOWDEN))
                .isEmpty();
        assertThat(cache.size())
                .isEqualTo(1);
    }

    @Test
    void putOnlyValue() {
        fail("You have to override this test");
    }

    @Test
    void get() {
        final LeveledCache<String, Movie> cache = createCache(3);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);
        cache.put(INCEPTION);

        assertThat(cache.size())
                .isEqualTo(3);
        assertThat(cache.get(null))
                .isNull();
        assertThat(cache.get(Movies.INCEPTION_IMDB))
                .isEqualTo(INCEPTION);
    }

    @Test
    final void getWithNull() {
        final LeveledCache<String, Movie> cache = createCache(10);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);
        cache.put(INCEPTION);

        assertThat(cache.size())
                .isEqualTo(3);
        assertThat(cache.get(null))
                .isNull();
    }

    @Test
    final void getWithNonExistingKey() {
        final LeveledCache<String, Movie> cache = createCache(1);
        cache.put(AQUAMAN);

        assertThat(cache.size())
                .isEqualTo(1);
        assertThat(cache.get(Movies.SNOWDEN_IMDB))
                .isNull();
    }

    @Test
    final void getFromEmptyCache() {
        final LeveledCache<String, Movie> cache = createCache(0);

        assertThat(cache.size())
                .isZero();
        assertThat(cache.get(Movies.SNOWDEN_IMDB))
                .isNull();
    }
}
