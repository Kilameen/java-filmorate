package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.genre.GenreService;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.service.film.rating.RatingService;
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
    private final RatingService ratingService;
    private static final LocalDate STARTED_REALISE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmServiceImpl(@Qualifier("H2FilmDb") FilmStorage filmStorage,
                           @Qualifier("H2UserDb") UserStorage userStorage,
                           LikeDbStorage likeDbStorage, GenreService genreService, RatingService ratingService) {

        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.genreService = genreService;
        this.ratingService = ratingService;
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
        validateGenreAndRating(film);
        validate(film);

        Film addFilm = filmStorage.create(film);
        if (nonNull(film.getGenres())) {
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
        validateGenreAndRating(film);

        //Валидация для обновления(проверка на существование фильма с таким ID)
        Film existingFilm = filmStorage.getFilm(film.getId());
        if (isNull(existingFilm)) {
            throw new NotFoundException("Фильма с таким id не существует");
        }

        Film updatedFilm = filmStorage.update(film);

        genreService.clearFilmGenres(film.getId());
        Set<Genre> genres = film.getGenres() == null ? new HashSet<>() : new HashSet<>(film.getGenres());
        if (nonNull(genres) && !genres.isEmpty()) {
            for (Genre genre : genres) {
                genreService.setGenre(film.getId(), genre.getId());
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
    }

    private void validateGenreAndRating(Film film) {
        if (isNull(film.getMpa()) || isNull(ratingService.getRatingByID(film.getMpa().getId()))) {
            throw new ValidationException("Рейтинг с таким id не существует");
        }

        if (nonNull(film.getGenres())) {
            for (Genre genre : film.getGenres()) {
                try {
                    genreService.getGenre(genre.getId());
                } catch (NotFoundException e) {
                    throw new NotFoundException("Жанра с id " + genre.getId() + " не существует");
                }
            }
        }
    }
}