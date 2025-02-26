package ru.yandex.practicum.filmorate.service.film;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    @Autowired
    private final FilmStorage filmStorage;
    @Autowired
    private final UserStorage userStorage;


    @Override
    public void addLike(Long filmId, Long userId) {
        validateUserId(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        validateUserId(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        if (count < 1) {
            throw new ValidationException("Значение переданного параметра количество записей должен быть больше 0");
        }
        return filmStorage.findAll()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(film -> film.getLikes().size())))
                .limit(count)
                .toList();
    }

    @Override

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public void deleteAllFilms(Film film) {
        filmStorage.deleteAllFilms(film);
    }

    private void validateUserId(Long id) {
        userStorage.getUserById(id);
    }
}