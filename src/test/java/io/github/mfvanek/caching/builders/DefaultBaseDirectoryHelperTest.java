/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.builders;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultBaseDirectoryHelperTest {

    @Test
    void forMacOs() {
        assertThat(DefaultBaseDirectoryHelper.forMacOs().toString())
                .endsWith(String.join(File.separator, List.of("Library", "Caches", "jcache")));
    }

    @Test
    void forLinux() {
        assertThat(DefaultBaseDirectoryHelper.forLinux().toString())
                .endsWith(String.join(File.separator, List.of("var", "tmp", "jcache")));
    }

    @Test
    void forWindows() {
        assertThat(DefaultBaseDirectoryHelper.forWindows().toString())
                .endsWith(File.separator + "jcache");
    }
}
