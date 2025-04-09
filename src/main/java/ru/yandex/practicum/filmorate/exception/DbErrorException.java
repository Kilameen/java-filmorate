package ru.yandex.practicum.filmorate.exception;

public class DbErrorException extends RuntimeException {
    public DbErrorException(String message) {
        super(message);
    }
}
