package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.Set;

public interface FilmService {

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film getFilmById(Long id);

    Set<Film> getDirectorFilms(Long directorId, String sortBy);

    void deleteFilmById(Long id);

    Collection<Film> getPopularFilms(Long count,Long genreId, Integer year);

    Collection<Film> getFilmByNameOrDirector(String keyWords, String searchParameter);

    Collection<Film> getCommonFilms(Long userId, Long friendId);
}