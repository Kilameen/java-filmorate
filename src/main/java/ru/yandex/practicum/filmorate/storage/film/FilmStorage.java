package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film getFilmById(Long id);

    Film create(Film film) throws MethodArgumentNotValidException;

    void deleteAllFilms(Film film);

    Film deleteFilm(Long id);

    Film update(Film updateFilm) throws MethodArgumentNotValidException;
}