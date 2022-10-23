/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.helpers;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@UtilityClass
public final class Serializer {

    @SneakyThrows
    public static <ValueType extends Serializable> Path serialize(final ValueType value, final Path cacheFilePath) {
        try (FileChannel channel = FileChannel.open(cacheFilePath, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream ous = new ObjectOutputStream(bos)) {
            ous.writeObject(value);
            channel.write(ByteBuffer.wrap(bos.toByteArray()));
        }
        return cacheFilePath;
    }

    @SneakyThrows
    public static <ValueType extends Serializable> ValueType deserialize(final Class<ValueType> type, final Path cacheFilePath) {
        final byte[] data = Files.readAllBytes(cacheFilePath);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return type.cast(ois.readObject());
        }
    }
}
