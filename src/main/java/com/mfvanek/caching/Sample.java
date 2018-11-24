/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching;

import com.mfvanek.caching.builders.TwoLevelsCacheBuilder;
import com.mfvanek.caching.helpers.DirectoryUtils;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Task:
 * Create a configurable two-level cache (for caching Objects).
 * Level 1 is memory, level 2 is filesystem.
 * Config params should let one specify the cache strategies and max sizes of level 1 and 2.
 */
class Sample {

    private static Path directoryForPersistenceCache;
    private static Cache<String, Movie> cache;
    private static final int FIRST_LEVEL_MAX_SIZE = 3;
    private static final int SECOND_LEVEL_MAX_SIZE = 10;
    private static final float FIRST_LEVEL_EVICTION_FACTOR = 0.5f; // Two items will be evicted from the cache when it becomes full
    private static final float SECOND_LEVEL_EVICTION_FACTOR = 0.3f;

    public static void main(String[] args) {
        try {
            System.out.println("Hello there! This is a caching demo app!");

            initCache();
            fillLevel("First", FIRST_LEVEL_MAX_SIZE);
            testLevel("First");
            increasingFrequencyOnFirstLevel();
            fillLevel("Second", SECOND_LEVEL_MAX_SIZE + FIRST_LEVEL_MAX_SIZE);
            testLevel("Second");
            fillWithEviction();

            System.out.println("\nClosing the app...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DirectoryUtils.deleteDirectory(directoryForPersistenceCache);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initCache() throws Exception {
        directoryForPersistenceCache = Files.createTempDirectory("jcache");
        System.out.println("\n=== Initializing the cache ===");
        System.out.println("Persistence cache will be stored in " + directoryForPersistenceCache);
        System.out.println("First level cache max size is " + FIRST_LEVEL_MAX_SIZE);
        System.out.println("Second level cache max size is " + SECOND_LEVEL_MAX_SIZE);

        final TwoLevelsCacheBuilder<String, Movie> builder = TwoLevelsCacheBuilder.getInstance(Movie.class);
        cache = builder.
                setBaseDirectory(directoryForPersistenceCache).
                setFirstLevelMaxSize(FIRST_LEVEL_MAX_SIZE).
                setSecondLevelMaxSize(SECOND_LEVEL_MAX_SIZE).
                setFirstLevelEvictionFactor(FIRST_LEVEL_EVICTION_FACTOR).
                setSecondLevelEvictionFactor(SECOND_LEVEL_EVICTION_FACTOR).
                build();
    }

    private static void fillLevel(String levelNumber, int limit) {
        System.out.println(String.format("\n=== Filling the cache with data. %s level ===", levelNumber));
        Movies.getAllMovies().stream().limit(limit).forEach(movie -> {
            try {
                System.out.println("Adding movie to the cache " + movie);
                List<Movie> evictedItems = cache.put(movie);
                if (CollectionUtils.isNotEmpty(evictedItems)) {
                    throw new RuntimeException("Error occurs when adding movie to the cache");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void testLevel(String levelNumber) {
        System.out.println(String.format("\n=== Testing the cache. %s level ===", levelNumber));
        Movies.getAllMovies().forEach(movie -> {
            try {
                final String movieId = movie.getIdentifier();
                if (cache.containsKey(movieId)) {
                    System.out.println("The cache contains movie " + movie);
                    System.out.println("   Value from the cache " + cache.get(movieId));
                } else {
                    System.out.println("! The cache doesn't contain movie " + movie);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void increasingFrequencyOnFirstLevel() throws Exception {
        System.out.println("\n=== Testing the cache. Frequency on First level ===");
        final String movieId = Movies.getAllMovies().get(0).getIdentifier();
        if (cache.containsKey(movieId)) {
            System.out.println("The cache contains movie " + cache.get(movieId));
        } else {
            throw new RuntimeException("The cache doesn't contain movie with IMDB id = " + movieId);
        }
    }

    private static void fillWithEviction() {
        System.out.println("\n=== Filling the cache with data eviction ===");
        final int count = Math.abs(FIRST_LEVEL_MAX_SIZE + SECOND_LEVEL_MAX_SIZE - Movies.getAllMovies().size()) + 1;
        Movies.getRandomGeneratedMovies(count).forEach(movie -> {
            try {
                System.out.println("Adding movie to the cache " + movie);
                List<Movie> evictedItems = cache.put(movie);
                if (CollectionUtils.isNotEmpty(evictedItems)) {
                    evictedItems.forEach(m -> System.out.println("!! Movie evicted from the cache " + m));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
