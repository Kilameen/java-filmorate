package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Marker;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate STARTED_REALISE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Список фильмов: {}", films);
        return films.values();
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film);

        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм {} добавлен", film);
        return film;
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public Film update(@Valid @RequestBody Film updateFilm) {
        log.info("Запрос на обновление информации о фильме {}", updateFilm);

        if (updateFilm.getId() == null) {
            log.error("Id фильма не указан!");
            throw new ValidationException("Id фильма должен быть указан!");
        }
        validate(updateFilm);
        if (films.containsKey(updateFilm.getId())) {
            Film oldFilmInformation = films.get(updateFilm.getId());
            oldFilmInformation.setName(updateFilm.getName());
            oldFilmInformation.setDescription(updateFilm.getDescription());
            oldFilmInformation.setReleaseDate(updateFilm.getReleaseDate());
            oldFilmInformation.setDuration(updateFilm.getDuration());

            log.info("Информация о фильме {} обновлена!", updateFilm);
            return oldFilmInformation;
        }
        log.error("Фильма с Id = {} не найдено.", updateFilm.getId());
        throw new NotFoundException("Фильма с Id = " + updateFilm.getId() + " не найдено.");
    }

    private void validate(Film film) {
        if (films.values()
                .stream()
                .anyMatch(f -> f.getName().equals(film.getName())
                        && f.getReleaseDate().equals(film.getReleaseDate())
                        && !Objects.equals(f.getId(), film.getId()))) {
            log.error("Фильм с названием {} и датой релиза {} уже существует", film.getName(), film.getReleaseDate());
            throw new DuplicatedDataException("Фильм с таким названием и датой релиза уже существует");
        }
        if (STARTED_REALISE_DATE.isAfter(film.getReleaseDate())) {
            log.error("Дата релиза фильма не может быть раньше: {}", STARTED_REALISE_DATE);
            throw new ValidationException("Дата релиза фильма раньше: " + STARTED_REALISE_DATE);
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}