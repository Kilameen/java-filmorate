package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film getFilmById(Long id);

    Film create(Film film);

    void deleteAllFilms(Film film);

    Film deleteFilm(Long id);

    Film update(Film updateFilm);
}