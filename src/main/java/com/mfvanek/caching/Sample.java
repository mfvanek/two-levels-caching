package com.mfvanek.caching;

import com.mfvanek.caching.builders.CacheBuilder;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;

public class Sample {

    public static void main(String[] args) {
        try {
            System.out.println("This is a caching demo app");

            final CacheBuilder<String, Movie> builder = CacheBuilder.getInstance();
            final Cache<String, Movie> cache = builder.build();

            final Movie snowden = Movies.getSnowden();
            cache.put(snowden.getIdentifier(), snowden);

            System.out.println("Press any key to exit...");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
