package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

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

    @GetMapping("/director/{directorId}")
    public Set<Film> getDirectorFilms(@PathVariable("directorId") Long directorId, @RequestParam(defaultValue = "year") String sortBy) {
        if (!sortBy.equals("year") && !sortBy.equals("likes")) {
            throw new IllegalArgumentException("Недопустимый параметр сортировки: " + sortBy);
        }
        log.info("Запрос на получение фильмов режиссера с id:{}", directorId);
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Long filmId) {
        log.info("Удаление фильма с id {}", filmId);
        filmService.deleteFilmById(filmId);
        log.info("Фильм с id {} успешно удалён", filmId);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Long userId,
                                           @RequestParam Long friendId) {
        log.info("Запрос на получение общих фильмов пользователей {} и {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilmByNameOrDirector(HttpServletRequest request) {
        String query = request.getParameter("query").trim();
        String by = request.getParameter("by").trim();

        if (query.isEmpty()) {
            throw new ValidationException("Параметр query не может быть пустым или отсутствовать");
        }
        if (by.isEmpty()) {
            throw new ValidationException("Параметр byList не может быть пустым или отсутствовать");
        }

        log.info("Поиск фильма, содержащего \"{}\" в {}", query, by);
        return filmService.getFilmByNameOrDirector(query, by);
    }
}