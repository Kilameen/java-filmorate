package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dao.event.EventDao;
import ru.yandex.practicum.filmorate.enums.SearchParameter;
import ru.yandex.practicum.filmorate.enums.SortType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class FilmServiceImpl implements FilmService {

    private final DirectorStorage directorStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDbStorage likeDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final RatingDbStorage ratingDbStorage;
    private final EventDao eventDao;
    private static final LocalDate STARTED_REALISE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmServiceImpl(DirectorStorage directorStorage, @Qualifier("H2FilmDb") FilmStorage filmStorage,
                           @Qualifier("H2UserDb") UserStorage userStorage,
                           @Qualifier("H2LikeDb") LikeDbStorage likeDbStorage,
                           @Qualifier("H2GenreDb") GenreDbStorage genreDbStorage,
                           @Qualifier("H2RatingDb") RatingDbStorage ratingDbStorage,
                           @Qualifier("H2EventDb") EventDao eventDao) {
        this.directorStorage = directorStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.ratingDbStorage = ratingDbStorage;
        this.eventDao = eventDao;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        validateUserId(userId);
        filmStorage.getFilm(filmId);
        likeDbStorage.addLike(filmId, userId);
        eventDao.create(userId, "LIKE", "ADD", filmId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        validateUserId(userId);
        likeDbStorage.deleteLike(filmId, userId);
        eventDao.create(userId, "LIKE", "REMOVE", filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(Long count, Long genreId, Integer year) {
        Collection<Film> popularFilms = filmStorage.getPopularFilms(count);
        Map<Long, Collection<Genre>> filmsGenres = genreDbStorage.getAllFilmsGenres(
                popularFilms.stream().map(Film::getId).collect(Collectors.toList())
        );
        popularFilms.forEach(film -> film.setGenres(filmsGenres.getOrDefault(film.getId(), Collections.emptyList())));

        if (count != null) {
            popularFilms = popularFilms.stream().limit(count).collect(Collectors.toList());
        }
        if (genreId != null && year == null) {
            popularFilms = popularFilms.stream()
                    .filter(film -> film.getGenres().stream()
                            .anyMatch(g -> g.getId().equals(genreId))).collect(Collectors.toList());
        } else if (year != null && genreId == null) {
            popularFilms = popularFilms.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .collect(Collectors.toList());
        } else if (genreId != null && year != null) {
            popularFilms = popularFilms.stream()
                    .filter(film -> film.getGenres().stream()
                            .anyMatch(g -> g.getId().equals(genreId)))
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .collect(Collectors.toList());
        }
        return popularFilms;
    }

    @Override
    public Film create(Film film) {
        validateRating(film);
        validate(film);

        Film addFilm = filmStorage.create(film);
        if (!film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                Director filmDirector = directorStorage.getDirectorById(director.getId()).orElseThrow(() -> new NotFoundException("Режиссер с id:" + director.getId() + " не найден"));
            }
        }

        if (film.getGenres() != null) {
            Set<Genre> genres = new HashSet<>(film.getGenres());
            List<Long> genreIds = genres.stream()
                    .map(Genre::getId)
                    .filter(genreId -> genreDbStorage.getGenre(genreId) != null)
                    .collect(Collectors.toList());
            genreDbStorage.setGenres(addFilm.getId(), genreIds);
            addFilm.setGenres(new ArrayList<>(genreDbStorage.getFilmGenres(addFilm.getId())));
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
        Collection<Genre> filmGenres = genreDbStorage.getFilmGenres(id);
        film.setGenres(filmGenres);
        return film;
    }

    @Override
    public Set<Film> getDirectorFilms(Long directorId, String sortBy) {
        sortBy = sortBy.replace(" ", "");

        SortType sortType = SortType.fromString(sortBy);

        List<Film> directorFilms = filmStorage.getDirectorFilms(directorId);
        if (directorFilms == null || directorFilms.isEmpty()) {
            throw new NotFoundException("Режиссер с ID " + directorId + " не найден или не имеет фильмов.");
        }
        switch (sortType) {
            case LIKES:
                return directorFilms.stream()
                        .sorted(Comparator.comparing(Film::getLikes).reversed()) // Сортируем по убыванию лайков
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            default:
                return directorFilms.stream()
                        .sorted(Comparator.comparing(Film::getReleaseDate)) // Сортируем по убыванию года
                        .collect(Collectors.toCollection(LinkedHashSet::new));
        }
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

    @Override
    public Collection<Film> getFilmByNameOrDirector(String keyWords, String searchParameter) {
        searchParameter = searchParameter.replace(" ", "");

        Collection<Film> films;
        SearchParameter parameter = SearchParameter.fromString(searchParameter);

        films = switch (parameter) {
            case DIRECTOR -> filmStorage.getFilmByDirector(keyWords);
            case TITLE -> filmStorage.getFilmByName(keyWords);
            default -> filmStorage.getFilmByNameOrDirector(keyWords);
        };

        Collection<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, Collection<Genre>> filmsGenres = genreDbStorage.getAllFilmsGenres(filmIds);
        for (Film film : films) {
            film.setGenres(filmsGenres.getOrDefault(film.getId(), Collections.emptyList()));
        }
        return films;
    }

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Пользователь и друг не могут быть одним и тем же человеком.");
        }

        validateUserId(userId);
        validateUserId(friendId);

        Collection<Film> films = filmStorage.getCommonFilms(userId, friendId);

        Collection<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Long, Collection<Genre>> filmGenres = genreDbStorage.getAllFilmsGenres(filmIds);

        for (Film film : films) {
            film.setGenres(filmGenres.getOrDefault(film.getId(), Collections.emptyList()));
        }

        return films;
    }
}