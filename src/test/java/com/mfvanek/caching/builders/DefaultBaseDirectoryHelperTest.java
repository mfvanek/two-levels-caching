/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.builders;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultBaseDirectoryHelperTest {

    @Test
    void forMacOs() {
        assertThat(DefaultBaseDirectoryHelper.forMacOs().toString())
                .startsWith("/Users/")
                .endsWith("/Library/Caches/jcache");
    }

    @Test
    void forLinux() {
        assertThat(DefaultBaseDirectoryHelper.forLinux())
                .hasToString("/var/tmp/jcache");
    }

    @Test
    void forWindows() {
        assertThat(DefaultBaseDirectoryHelper.forWindows())
                .hasToString("/jcache");
    }
}
