package ru.yandex.practicum.filmorate.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

public final class Reader {
    public Reader() {
        throw new RuntimeException("Попытка создать окончательный класс");
    }

    public static String readString(String filePath) throws NoSuchElementException {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException ioException) {
            throw new RuntimeException();
        }
    }
}