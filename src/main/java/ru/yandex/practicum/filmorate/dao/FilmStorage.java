package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film getFilm(Long id);

    Collection<Film> getPopularFilms(Long count);

    List<Film> getDirectorFilms(Long directorId);

    boolean deleteFilm(Long id);

    Collection<Film> getFilmByName(String keyWords);

    Collection<Film> getFilmByDirector(String keyWords);

    Collection<Film> getFilmByNameOrDirector(String keyWords);

    Collection<Film> getCommonFilms(Long userId, Long friendId);

}