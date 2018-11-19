package com.mfvanek.caching.models;

import com.mfvanek.caching.interfaces.Cacheable;

public class Movie implements Cacheable<String> {

    private final String title;
    private final int year;
    private final String imdb;

    public Movie(String title, int year, String imdb) {
        this.title = title;
        this.year = year;
        this.imdb = imdb;
    }

    public int getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getIdentifier() {
        return imdb;
    }
}
