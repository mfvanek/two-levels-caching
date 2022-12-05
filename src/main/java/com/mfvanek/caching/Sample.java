/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching;

import com.mfvanek.caching.builders.TwoLevelsCacheBuilder;
import com.mfvanek.caching.helpers.DirectoryUtils;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Task:
 * Create a configurable two-level cache (for caching Objects).
 * Level 1 is memory, level 2 is filesystem.
 * Config params should let one specify the cache strategies and max sizes of level 1 and 2.
 */
@Slf4j
class Sample {

    private static Path directoryForPersistenceCache;
    private static Cache<String, Movie> cache;
    private static final int FIRST_LEVEL_MAX_SIZE = 3;
    private static final int SECOND_LEVEL_MAX_SIZE = 10;
    private static final float FIRST_LEVEL_EVICTION_FACTOR = 0.5f; // Two items will be evicted from the cache when it becomes full
    private static final float SECOND_LEVEL_EVICTION_FACTOR = 0.3f;

    public static void main(final String[] args) {
        try {
            log.info("Hello there! This is a caching demo app!");

            initCache();
            fillLevel("First", FIRST_LEVEL_MAX_SIZE);
            testLevel("First");
            increasingFrequencyOnFirstLevel();
            fillLevel("Second", SECOND_LEVEL_MAX_SIZE + FIRST_LEVEL_MAX_SIZE);
            testLevel("Second");
            fillWithEviction();

            log.info("%nClosing the app...");
        } finally {
            DirectoryUtils.deleteDirectory(directoryForPersistenceCache);
        }
    }

    @SneakyThrows
    private static void initCache() {
        directoryForPersistenceCache = Files.createTempDirectory("jcache");
        log.info("%n=== Initializing the cache ===");
        log.info("Persistence cache will be stored in {}", directoryForPersistenceCache);
        log.info("First level cache max size is {}", FIRST_LEVEL_MAX_SIZE);
        log.info("Second level cache max size is {}", SECOND_LEVEL_MAX_SIZE);

        cache = TwoLevelsCacheBuilder.getInstance(Movie.class)
                .setBaseDirectory(directoryForPersistenceCache)
                .setFirstLevelMaxSize(FIRST_LEVEL_MAX_SIZE)
                .setSecondLevelMaxSize(SECOND_LEVEL_MAX_SIZE)
                .setFirstLevelEvictionFactor(FIRST_LEVEL_EVICTION_FACTOR)
                .setSecondLevelEvictionFactor(SECOND_LEVEL_EVICTION_FACTOR)
                .build();
    }

    private static void fillLevel(final String levelNumber, final int limit) {
        log.info("%n=== Filling the cache with data. {} level ===%n", levelNumber);
        Movies.getAllMovies().stream().limit(limit).forEach(movie -> {
            log.info("Adding movie to the cache {}", movie);
            final List<Movie> evictedItems = cache.put(movie);
            if (CollectionUtils.isNotEmpty(evictedItems)) {
                throw new RuntimeException("Error occurs when adding movie to the cache");
            }
        });
    }

    private static void testLevel(final String levelNumber) {
        log.info("%n=== Testing the cache. {} level ===%n", levelNumber);
        Movies.getAllMovies().forEach(movie -> {
            final String movieId = movie.getIdentifier();
            if (cache.containsKey(movieId)) {
                log.info("The cache contains movie {}", movie);
                log.info("   Value from the cache {}", cache.get(movieId));
            } else {
                log.warn("! The cache doesn't contain movie {}", movie);
            }
        });
    }

    private static void increasingFrequencyOnFirstLevel() {
        log.info("%n=== Testing the cache. Frequency on First level ===");
        final String movieId = Movies.getAllMovies().get(0).getIdentifier();
        if (cache.containsKey(movieId)) {
            final Movie movie = cache.get(movieId);
            log.info("The cache contains movie {}", movie);
        } else {
            throw new RuntimeException("The cache doesn't contain movie with IMDB id = " + movieId);
        }
    }

    private static void fillWithEviction() {
        log.info("%n=== Filling the cache with data eviction ===");
        final int count = Math.abs(FIRST_LEVEL_MAX_SIZE + SECOND_LEVEL_MAX_SIZE - Movies.getAllMovies().size()) + 1;
        Movies.getRandomGeneratedMovies(count).forEach(movie -> {
            log.info("Adding movie to the cache {}", movie);
            final List<Movie> evictedItems = cache.put(movie);
            if (CollectionUtils.isNotEmpty(evictedItems)) {
                evictedItems.forEach(m -> log.info("!! Movie evicted from the cache {}", m));
            }
        });
    }
}
