/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;

class Sample {

    private static Cache<String, Movie> cache;

    public static void main(String[] args) {
        try {
            System.out.println("This is a caching demo app");

            final CacheBuilder<String, Movie> builder = CacheBuilder.getInstance();
            cache = builder.setMaxSize(3).build();

            fillCache();
            testCache();

            System.out.println("Closing app...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fillCache() {
        final Movie snowden = Movies.getSnowden();
        cache.put(snowden.getIdentifier(), snowden);
        cache.put(snowden);
    }

    private static void testCache() {
        System.out.println("=== Testing cache ===");
        final Movie snowden = cache.get(Movies.SNOWDEN_IMDB);
        System.out.println("From cache = " + snowden);
    }
}
