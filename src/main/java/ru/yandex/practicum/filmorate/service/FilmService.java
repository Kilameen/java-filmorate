package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {

    @Autowired
    private final FilmStorage filmStorage;
    @Autowired
    private final UserStorage userStorage;

    public void addLike(Long filmId, Long userId) {
        validateUserId(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        validateUserId(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
    }

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

    private void validateUserId(Long id) {
        userStorage.getUserById(id);
    }
}