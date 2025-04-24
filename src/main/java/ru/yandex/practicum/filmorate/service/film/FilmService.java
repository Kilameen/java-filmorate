package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.Set;

public interface FilmService {

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Collection<Film> getPopularFilms(Long count);

    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film getFilmById(Long id);

    Set<Film> getDirectorFilms(Long directorId, String sortBy);
}