package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final int MAX_DESCRIPTION_LENGTH = 200;
    private final Map<Long, Film> films = new HashMap<>();

@GetMapping
public Collection<Film> findAll(){
return films.values();
}

@PostMapping
    public Film create(@RequestBody Film film) {

    if (film.getName() == null || film.getName().isBlank()) {
        throw new ValidationException("Название не может быть пустым!");
    }
    if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
        throw new ArithmeticException("Максимальная длина описания — 200 символов");
    }
}
}
