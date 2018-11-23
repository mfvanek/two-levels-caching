/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MovieTest {

    @Test
    void equals() {
        final Movie first = Movies.getSnowden();
        final Movie second = Movies.getSnowden();
        final Movie third = Movies.getAquaman();

        assertEquals(first, second);
        assertEquals(first, first);
        assertNotEquals(first, third);
        assertNotEquals(second, third);
    }
}