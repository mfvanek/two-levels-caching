/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class Serializer {

    private static final Logger logger = LoggerFactory.getLogger(Serializer.class);

    private Serializer() {}

    public static <ValueType extends Serializable> Path serialize(final ValueType value, final Path cacheFilePath)
            throws IOException {
        try (FileChannel channel = FileChannel.open(cacheFilePath, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream ous = new ObjectOutputStream(bos)) {
            ous.writeObject(value);
            channel.write(ByteBuffer.wrap(bos.toByteArray()));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return cacheFilePath;
    }

    public static <ValueType extends Serializable> ValueType deserialize(final Class<ValueType> type, final Path cacheFilePath)
            throws IOException, ClassNotFoundException {
        final byte[] data = Files.readAllBytes(cacheFilePath);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return type.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
