/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.helpers.DirectoryUtils;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

abstract class BaseCacheTest {

    protected static final int MAX_SIZE = 2;
    protected static final Movie SNOWDEN = Movies.getSnowden();
    protected static final Movie AQUAMAN = Movies.getAquaman();
    protected static final Movie INCEPTION = Movies.getInception();
    protected static final Movie INTERSTELLAR = Movies.getInterstellar();
    protected static final Movie ARRIVAL = Movies.getArrival();
    protected static Path tempDir;

    protected abstract Cache<String, Movie> createCache() throws Exception;

    protected abstract Cache<String, Movie> createCache(int maxSize) throws Exception;

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
    void size() throws Exception {
        final Cache<String, Movie> cache = createCache();
        assertEquals(0, cache.size());

        cache.put(SNOWDEN);
        assertEquals(1, cache.size());

        cache.put(AQUAMAN);
        assertEquals(MAX_SIZE, cache.size());

        cache.put(INCEPTION);
        assertEquals(MAX_SIZE, cache.size());
    }

    @Test
    final void clear() throws Exception {
        final Cache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);
        cache.put(AQUAMAN);
        assertEquals(MAX_SIZE, cache.size());

        cache.clear();
        assertEquals(0, cache.size());
        assertFalse(cache.containsKey(Movies.SNOWDEN_IMDB));
        assertFalse(cache.containsKey(Movies.AQUAMAN_IMDB));
    }

    @Test
    final void containsKey() throws Exception {
        final Cache<String, Movie> cache = createCache(3);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);
        cache.put(INCEPTION);

        assertFalse(cache.containsKey(null));
        assertFalse(cache.containsKey(""));
        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
    }

    @Test
    final void removeNotExisting() throws Exception {
        final Cache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);
        cache.put(AQUAMAN);
        assertEquals(MAX_SIZE, cache.size());

        final Movie deleted = cache.remove("not existing key");
        assertEquals(MAX_SIZE, cache.size());
        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
        assertTrue(cache.containsKey(Movies.AQUAMAN_IMDB));
        assertNull(deleted);
    }

    @Test
    final void remove() throws Exception {
        final Cache<String, Movie> cache = createCache();
        cache.put(SNOWDEN);
        cache.put(AQUAMAN);
        assertEquals(MAX_SIZE, cache.size());

        Movie deleted = cache.remove(Movies.AQUAMAN_IMDB);
        assertEquals(1, cache.size());
        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
        assertFalse(cache.containsKey(Movies.AQUAMAN_IMDB));
        assertEquals(AQUAMAN, deleted);

        deleted = cache.remove(Movies.SNOWDEN_IMDB);
        assertEquals(0, cache.size());
        assertFalse(cache.containsKey(Movies.SNOWDEN_IMDB));
        assertEquals(SNOWDEN, deleted);
    }

    @Test
    final void putWithKey() throws Exception {
        final Cache<String, Movie> cache = createCache();
        List<Map.Entry<String, Movie>> evictedItems = cache.put(SNOWDEN.getIdentifier(), SNOWDEN);

        assertTrue(cache.containsKey(Movies.SNOWDEN_IMDB));
        assertEquals(0, evictedItems.size());
    }

    @Test
    void putTheSameValue() throws Exception {
        final Cache<String, Movie> cache = createCache();
        List<Movie> evictedItems = cache.put(SNOWDEN);
        assertEquals(1, cache.size());
        assertEquals(0, evictedItems.size());

        evictedItems = cache.put(SNOWDEN);
        assertEquals(1, cache.size());
        assertEquals(0, evictedItems.size());
    }

    @Test
    void putOnlyValue() throws Exception {
        fail("You have to override this test");
    }

    @Test
    void get() throws Exception {
        final Cache<String, Movie> cache = createCache(3);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);
        cache.put(INCEPTION);

        assertNull(cache.get(null));
        assertEquals(INCEPTION, cache.get(Movies.INCEPTION_IMDB));
    }

    @Test
    final void getWithNull() throws Exception {
        final Cache<String, Movie> cache = createCache(10);
        cache.put(AQUAMAN);
        cache.put(SNOWDEN);
        cache.put(INCEPTION);

        assertNull(cache.get(null));
    }

    @Test
    final void getWithNonExistingKey() throws Exception {
        final Cache<String, Movie> cache = createCache(1);
        cache.put(AQUAMAN);

        assertNull(cache.get(Movies.SNOWDEN_IMDB));
    }

    @Test
    final void getFromEmptyCache() throws Exception {
        final Cache<String, Movie> cache = createCache(0);

        assertNull(cache.get(Movies.SNOWDEN_IMDB));
    }
}
