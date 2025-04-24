package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.Objects.isNull;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDbStorage likeDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final RatingDbStorage ratingDbStorage;
    private static final LocalDate STARTED_REALISE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmServiceImpl(@Qualifier("H2FilmDb") FilmStorage filmStorage,
                           @Qualifier("H2UserDb") UserStorage userStorage,
                           @Qualifier("H2LikeDb") LikeDbStorage likeDbStorage, @Qualifier("H2GenreDb") GenreDbStorage genreDbStorage, @Qualifier("H2RatingDb") RatingDbStorage ratingDbStorage) {

        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.ratingDbStorage = ratingDbStorage;
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
        Collection<Film> popularFilms = filmStorage.getPopularFilms(count);
        Collection<Long> filmIds = popularFilms.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, Collection<Genre>> filmsGenres = genreDbStorage.getAllFilmsGenres(filmIds);
        for (Film film : popularFilms) {
            film.setGenres(filmsGenres.getOrDefault(film.getId(), Collections.emptyList()));
        }
        return popularFilms;
    }


    @Override
    public Film create(Film film) {
        validateRating(film);
        validate(film);

        Film addFilm = filmStorage.create(film);

        if (film.getGenres() != null) {
            Set<Genre> genres = new HashSet<>(film.getGenres());
            List<Long> genreIds = genres.stream()
                    .map(Genre::getId)
                    .filter(genreId -> genreDbStorage.getGenre(genreId) != null)
                    .collect(Collectors.toList());
            genreDbStorage.setGenres(addFilm.getId(), genreIds);
            addFilm.setGenres(new ArrayList<>(genres));
        }
        return addFilm;
    }

    @Override
    public Film update(Film film) {
        validateRating(film);

        Film existingFilm = filmStorage.getFilm(film.getId());
        if (isNull(existingFilm)) {
            throw new NotFoundException("Фильма с таким id не существует");
        }

        Film updatedFilm = filmStorage.update(film);

        genreDbStorage.clearFilmGenres(film.getId());
        Set<Genre> genres = new HashSet<>(film.getGenres());
        if (!genres.isEmpty()) {
            List<Long> genreIds = genres.stream()
                    .map(Genre::getId)
                    .filter(genreId -> genreDbStorage.getGenre(genreId) != null)
                    .collect(Collectors.toList());
            genreDbStorage.setGenres(film.getId(), genreIds);
        }
        return updatedFilm;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> allFilms = filmStorage.findAll();
        Collection<Long> filmIds = allFilms.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, Collection<Genre>> filmsGenres = genreDbStorage.getAllFilmsGenres(filmIds);
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
        Collection<Genre> filmGenres = genreDbStorage.getFilmGenres(id);
        film.setGenres(filmGenres);
        return film;
    }

    private void validateUserId(Long id) {
        userStorage.getUserById(id);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(STARTED_REALISE_DATE)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше: " + STARTED_REALISE_DATE);
        }
    }

    private void validateRating(Film film) {
        if (isNull(film.getMpa()) || isNull(ratingDbStorage.getRating(film.getMpa().getId()))) {
            throw new ValidationException("Рейтинг с таким id не существует");
        }
    }

    public void deleteFilmById(Long id) {
        filmStorage.getFilm(id);

        filmStorage.deleteFilm(id);
    }
}