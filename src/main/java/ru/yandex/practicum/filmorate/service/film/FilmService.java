package ru.yandex.practicum.filmorate.service.film;

import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmService {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Collection<Film> getPopularFilms(Long count);

    Film create(Film film) throws MethodArgumentNotValidException;

    Film update(Film film) throws MethodArgumentNotValidException;

    Collection<Film> findAll();

    Film getFilmById(Long id);

    void deleteAllFilms(Film film);
}
