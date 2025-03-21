package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    //Storage

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на получение фильмов");
        Collection<Film> filmCollection = filmService.findAll();
        log.debug("Список фильмов: {}", filmCollection);
        return filmCollection;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Запрос на получение фильма по id");
        return filmService.getFilmById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnCreate.class)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film);
        return filmService.create(film);
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public Film update(@Valid @RequestBody Film updateFilm) {
        log.info("Запрос на обновление информации о фильме {}", updateFilm);
        return filmService.update(updateFilm);
    }

    @DeleteMapping
    public void deleteAllFilm(Film film) {
        log.info("Запрос на удаление всех фильмов");
        filmService.deleteAllFilms(film);
        log.info("Все фильмы удалены");
    }

    //Service

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        log.info("Пользователь {} хочет поставить лайк фильму {}.", userId, filmId);
        filmService.addLike(filmId, userId);
        log.info("Фильму {} поставили лайк.", filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Long count) {
        log.info("Выполняется запрос на список популярных фильмов");
        if (count <= 0) {
            throw new ValidationException("Значение переданного параметра количество записей должен быть больше 0");
        }
        return filmService.getPopularFilms(count);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        log.info("Пользователь {} хочет удалить лайк фильму {}.", userId, filmId);
        filmService.deleteLike(filmId, userId);
        log.info("Лайк удален");
    }
}