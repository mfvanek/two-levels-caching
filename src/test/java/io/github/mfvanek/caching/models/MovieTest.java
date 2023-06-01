/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovieTest {

    @Test
    void equalsAndHashCodeAdhereContract() {
        final Movie first = Movies.getSnowden();
        final Movie second = Movies.getSnowden();
        final Movie third = Movies.getAquaman();

        assertThat(first)
                .isEqualTo(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(first)
                .hasSameHashCodeAs(second)
                .isNotEqualTo(third)
                .doesNotHaveSameHashCodeAs(third);
        assertThat(second)
                .isNotEqualTo(third)
                .doesNotHaveSameHashCodeAs(third);
    }
}
