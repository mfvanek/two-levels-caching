package com.mfvanek.caching.models;

public class Movies {

    private Movies() {}

    public static Movie getSnowden() {
        return new Movie("Snowden", 2016, "tt3774114");
    }
}
