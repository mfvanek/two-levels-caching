/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.models;

import com.mfvanek.caching.interfaces.Cacheable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public final class Movie implements Cacheable<String>, Serializable {

    private static final long serialVersionUID = 7524472395622976117L;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj.getClass() != getClass()) {
            return false;
        }

        Movie rhs = (Movie) obj;
        return new EqualsBuilder()
                .append(title, rhs.title)
                .append(year, rhs.year)
                .append(imdb, rhs.imdb)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 23).
                append(title).
                append(year).
                append(imdb).
                toHashCode();
    }

    @Override
    public String toString() {
        return String.format("Movie:{title:'%s', year:%s, imdb:'%s'}", getTitle(), getYear(), getIdentifier());
    }
}
