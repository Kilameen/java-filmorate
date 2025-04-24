package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component(value = "H2FilmDb")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final DirectorStorage directorStorage;
    private final RatingDbStorage ratingDbStorage;
    private static final String INSERT_FILM_SQL_REQUEST = "INSERT INTO films (film_name, description, release_date, duration, mpa_id)\n" +
            "VALUES (?, ?, ?, ?, ?);";
    private static final String UPDATE_FILM_SQL_REQUEST = "UPDATE films\n" +
            "SET film_name = ?,\n" +
            "    description = ?,\n" +
            "    release_date = ?,\n" +
            "    duration = ?,\n" +
            "    mpa_id = ?\n" +
            "WHERE film_id = ?;";
    private static final String SELECT_ALL_FILM_SQL_REQUEST =
            "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate, d.director_id, d.name AS director_name\n" +
                    "FROM films AS f\n" +
                    "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id\n" +
                    "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
                    "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id\n" +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
                    "GROUP BY f.film_id, r.rating_id, r.rating_name, d.director_id, d.name\n" +
                    "ORDER BY f.film_id;\n";
    private static final String SELECT_FILM_BY_ID_SQL_REQUEST =
            "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate, d.director_id, d.name AS director_name\n" +
                    "FROM films AS f\n" +
                    "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id\n" +
                    "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
                    "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id\n" +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
                    "WHERE f.film_id = ?\n" +
                    "GROUP BY f.film_id, r.rating_id, r.rating_name, d.director_id, d.name;\n";

    private static final String SELECT_POPULAR_FILMS_SQL_REQUEST = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate, d.director_id, d.name AS director_name\n" +
            "FROM films f\n" +
            "LEFT JOIN rating_mpa r ON f.mpa_id = r.rating_id\n" +
            "LEFT JOIN film_likes fl ON f.film_id = fl.film_id\n" +
            "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id\n" +
            "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
            "GROUP BY f.film_id, r.rating_id, r.rating_name\n" +
            "ORDER BY rate DESC\n" +
            "LIMIT ?;\n";
    private static final String SELECT_FILM_BY_DIRECTOR =
            "SELECT f.*,r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate,d.name,d.director_id " +
                    "FROM films AS f " +
                    "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN directors AS d ON d.director_id = fd.director_id " +
                    "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id\n" +
                    "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
                    "WHERE fd.director_id = ?" +
                    "GROUP BY f.film_id, r.rating_id, r.rating_name, d.director_id, d.name;\n";

    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_FILM_SQL_REQUEST, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setLong(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);

        Rating mpa = ratingDbStorage.getRating(film.getMpa().getId());
        if (mpa == null) {
            throw new NotFoundException("Mpa c id:" + film.getMpa().getId() + " не найден");
        }
        film.setMpa(mpa);

        Long filmId = keyHolder.getKeyAs(Long.class);
        film.setId(filmId);
        saveFilmDirectors(filmId, film.getDirector().stream().collect(Collectors.toSet()));
        List<Director> filmDirectors = directorStorage.getFilmDirectors(film.getId());
        film.setDirector(filmDirectors.stream().collect(Collectors.toSet()));

        return film;
    }

    private void saveFilmDirectors(Long filmId, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (Director director : directors) {
            batch.add(new Object[]{filmId, director.getId()});
        }

        jdbcTemplate.batchUpdate(sql, batch);
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(UPDATE_FILM_SQL_REQUEST,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return getFilm(film.getId());
    }

    @Override
    public Collection<Film> findAll() {
        return jdbcTemplate.query(SELECT_ALL_FILM_SQL_REQUEST, filmMapper);
    }

    @Override
    public Film getFilm(Long id) {
        return jdbcTemplate.query(SELECT_FILM_BY_ID_SQL_REQUEST, filmMapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return jdbcTemplate.query(SELECT_POPULAR_FILMS_SQL_REQUEST, filmMapper, count);
    }

    @Override
    public List<Film> getDirectorFilms(Long directorId) {
        return jdbcTemplate.query(SELECT_FILM_BY_DIRECTOR, filmMapper, directorId);
    }


}