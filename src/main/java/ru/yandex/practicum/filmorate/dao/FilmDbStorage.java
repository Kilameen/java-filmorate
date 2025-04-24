package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

@Component(value = "H2FilmDb")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private static final String INSERT_FILM_SQL_REQUEST = "INSERT INTO films (film_name, description, release_date, duration, mpa_id)\n" +
            "VALUES (?, ?, ?, ?, ?);";
    private static final String UPDATE_FILM_SQL_REQUEST = "UPDATE films\n" +
            "SET film_name = ?,\n" +
            "    description = ?,\n" +
            "    release_date = ?,\n" +
            "    duration = ?,\n" +
            "    mpa_id = ?\n" +
            "WHERE film_id = ?;";
    private static final String SELECT_ALL_FILM_SQL_REQUEST = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate\n" +
            "FROM films AS f\n" +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id\n" +
            "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
            "GROUP BY f.film_id, r.rating_id\n" +
            "ORDER BY f.film_id;\n";
    private static final String SELECT_FILM_BY_ID_SQL_REQUEST = "SELECT films.*, rating_mpa.*, COUNT(film_likes.user_id) AS rate\n" +
            "   FROM films\n" +
            "   LEFT JOIN rating_mpa ON films.mpa_id = rating_mpa.rating_id\n" +
            "   LEFT JOIN film_likes ON films.film_id = film_likes.film_id\n" +
            "   WHERE films.film_id = ?\n" +
            "   GROUP BY films.film_id;\n";
    private static final String SELECT_POPULAR_FILMS_SQL_REQUEST = "SELECT f.*, r.rating_id, r.rating_name, COUNT(fl.user_id) AS rate\n" +
            "FROM films f\n" +
            "LEFT JOIN rating_mpa r ON f.mpa_id = r.rating_id\n" +
            "LEFT JOIN film_likes fl ON f.film_id = fl.film_id\n" +
            "GROUP BY f.film_id, r.rating_id, r.rating_name\n" +
            "ORDER BY rate DESC\n" +
            "LIMIT ?;\n";
    private static final String DELETE_FILM_SQL_REQUEST = "DELETE FROM films\n" +
            "WHERE film_id = ?;";

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_FILM_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setLong(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);
        return getFilm(Objects.requireNonNull(keyHolder.getKey()).longValue());
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
    public void deleteFilm(Long id) {
        getFilm(id);

        jdbcTemplate.update(DELETE_FILM_SQL_REQUEST, id);
    }
}