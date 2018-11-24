/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public final class DirectoryUtils {

    private DirectoryUtils() {}

    public static void deleteDirectory(Path directoryToDelete) throws IOException {
        if (Files.exists(directoryToDelete) && Files.isDirectory(directoryToDelete)) {
            Files.walk(directoryToDelete)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
