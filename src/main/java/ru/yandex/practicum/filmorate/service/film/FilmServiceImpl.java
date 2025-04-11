package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.genre.GenreService;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDbStorage likeDbStorage;
    private final GenreService genreService;
    private static final LocalDate STARTED_REALISE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmServiceImpl(@Qualifier("H2FilmDb") FilmStorage filmStorage,
                           @Qualifier("H2UserDb") UserStorage userStorage,
                           LikeDbStorage likeDbStorage, GenreService genreService) {

        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.genreService = genreService;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        validateUserId(userId);
        likeDbStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        validateUserId(userId);
        likeDbStorage.deleteLike(filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public Film create(Film film) {
        validate(film); // Проверяем основные параметры фильма

        Film addFilm = filmStorage.create(film);
        if (nonNull(film.getGenres())) {
            // Используем Set для исключения дубликатов genreId
            Set<Genre> genres = new HashSet<>(film.getGenres());
            for (Genre genre : genres) {
                genreService.setGenre(addFilm.getId(), genre.getId());
            }
            addFilm.setGenres(genreService.getFilmGenres(addFilm.getId()));
        }
        return addFilm;
    }

    @Override
    public Film update(Film film) {
        validate(film); // Проверяем, что данные фильма корректны

        Film updatedFilm = filmStorage.update(film); // Обновляем информацию о фильме
        if (isNull(updatedFilm)) {
            throw new NotFoundException("Фильма с таким id не существует");
        }

        genreService.clearFilmGenres(film.getId()); // Очищаем текущие жанры фильма

        Set<Genre> genres = new HashSet<>(film.getGenres()); // Преобразуем список в HashSet, чтобы исключить дубликаты
        if (nonNull(genres) && !genres.isEmpty()) { // Проверяем, что жанры не null и не пусты
            for (Genre genre : genres) {
                genreService.setGenre(film.getId(), genre.getId()); // Устанавливаем жанр для фильма
            }
        }
        return updatedFilm;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> allFilms = filmStorage.findAll();
        Collection<Long> filmIds = allFilms.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, Collection<Genre>> filmsGenres = genreService.getAllFilmsGenres(filmIds);
        for (Film film : allFilms) {
            film.setGenres(filmsGenres.getOrDefault(film.getId(), Collections.emptyList()));
        }
        return allFilms;
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilm(id);
        if (isNull(film)) {
            throw new NotFoundException("Фильма с таким id не существует");
        }
        Collection<Genre> filmGenres = genreService.getFilmGenres(id);
        film.setGenres(filmGenres);
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        filmStorage.deleteFilm(film);
        genreService.clearFilmGenres(film.getId());
    }

    private void validateUserId(Long id) {
        userStorage.getUserById(id);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(STARTED_REALISE_DATE)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше: " + STARTED_REALISE_DATE);
        }
        if (filmStorage.findAll().stream().anyMatch(f ->
                f.getName().equals(film.getName()) && f.getReleaseDate().equals(film.getReleaseDate()))) {
            throw new DuplicatedDataException("Фильм с таким названием и датой релиза уже существует.");
        }
    }
}