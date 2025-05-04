package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.CustomFilmExtractor;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.NewFilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Component(value = "H2FilmDb")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    private static final String INSERT_FILM_SQL = "INSERT INTO films (film_name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?);";
    private static final String UPDATE_FILM_SQL = "UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?;";
    private static final String SELECT_ALL_FILMS_SQL = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate " +
            "FROM films AS f " +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id " +
            "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
            "GROUP BY f.film_id, r.rating_id, r.rating_name " +
            "ORDER BY f.film_id;";

    private static final String SELECT_FILM_BY_ID_SQL = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate\n" +
            "FROM films AS f\n" +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id\n" +
            "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
            "WHERE f.film_id = ?\n" +
            "GROUP BY f.film_id, r.rating_id";

    private static final String SELECT_POPULAR_FILMS_SQL = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate\n" +
            "FROM films f LEFT JOIN rating_mpa r ON f.mpa_id = r.rating_id\n" +
            "LEFT JOIN film_likes fl ON f.film_id = fl.film_id\n" +
            "GROUP BY f.film_id, r.rating_id, r.rating_name\n" +
            "ORDER BY rate DESC";
    private static final String SELECT_FILM_BY_DIRECTOR_SQL = "SELECT f.*,r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate,d.name,d.director_id\n" +
            "FROM films AS f\n" +
            "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id\n" +
            "LEFT JOIN directors AS d ON d.director_id = fd.director_id\n" +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id\n" +
            "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id WHERE fd.director_id = ?\n" +
            "GROUP BY f.film_id, r.rating_id, r.rating_name, d.director_id, d.name;";
    private static final String DELETE_FILM_SQL_REQUEST = "DELETE FROM films\n" +
            "WHERE film_id = ?;";
    private static final String SELECT_FILMS_WITH_KEY_WORD_BY_NAME_SQL = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate, d.director_id, d.name AS director_name\n" +
            "FROM films AS f " +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id\n" +
            "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
            "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id\n" +
            "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
            "WHERE LOWER(f.FILM_NAME) LIKE LOWER(?)\n" +
            "GROUP BY f.film_id, r.rating_id, r.rating_name\n" +
            "ORDER BY rate DESC;";
    private static final String SELECT_FILMS_WITH_KEY_WORD_BY_DIRECTOR_SQL = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate, d.director_id, d.name AS director_name\n" +
            "FROM films AS f " +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id\n" +
            "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
            "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id\n" +
            "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
            "WHERE LOWER(d.name) LIKE LOWER(?)\n" +
            "GROUP BY f.film_id, r.rating_id, r.rating_name\n" +
            "ORDER BY rate DESC;";
    private static final String SELECT_FILMS_WITH_KEY_WORD_BY_DIRECTOR_AND_NAME_SQL = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate, d.director_id, d.name AS director_name\n" +
            "FROM films AS f " +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id\n" +
            "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
            "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id\n" +
            "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
            "WHERE LOWER(d.name) LIKE LOWER(?) OR LOWER(f.FILM_NAME) LIKE LOWER(?)\n" +
            "GROUP BY f.film_id, r.rating_id, r.rating_name\n" +
            "ORDER BY rate DESC;";
    private static final String SELECT_COMMON_FILMS_SQL = "SELECT f.*, r.rating_name, r.rating_id,\n" +
            "(SELECT COUNT(*) FROM film_likes fl WHERE fl.film_id = f.film_id) AS rate,\n" +
            "d.director_id, d.name, g.genre_id, g.genre_name\n" +
            "FROM films AS f\n" +
            "JOIN film_likes AS l ON f.film_id = l.film_id\n" +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id\n" +
            "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id\n" +
            "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
            "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id\n" +
            "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id\n" +
            "WHERE l.user_id IN (?, ?)\n" +
            "GROUP BY f.film_id, r.rating_id, r.rating_name, d.director_id, d.name, g.genre_id, g.genre_name\n" +
            "HAVING COUNT(DISTINCT l.user_id) = 2\n" +
            "ORDER BY rate DESC;\n";


    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_FILM_SQL,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setLong(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);

        Long filmId = keyHolder.getKeyAs(Long.class);
        film.setId(filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        int rowsAffected = jdbcTemplate.update(UPDATE_FILM_SQL,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return jdbcTemplate.query(SELECT_ALL_FILMS_SQL, filmMapper);
    }

    @Override
    public Film getFilm(Long id) {
        return jdbcTemplate.query(SELECT_FILM_BY_ID_SQL, filmMapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return jdbcTemplate.query(SELECT_POPULAR_FILMS_SQL, filmMapper);
    }

    @Override
    public List<Film> getDirectorFilms(Long directorId) {
        return jdbcTemplate.query(SELECT_FILM_BY_DIRECTOR_SQL, filmMapper, directorId);
    }

    @Override
    public boolean deleteFilm(Long id) {
        int rowsAffected = jdbcTemplate.update(DELETE_FILM_SQL_REQUEST, id);
        return rowsAffected > 0;
    }

    @Override
    public Collection<Film> getFilmByName(String keyWords) {
        return jdbcTemplate.query(SELECT_FILMS_WITH_KEY_WORD_BY_NAME_SQL, filmMapper, "%" + keyWords + "%");
    }

    @Override
    public Collection<Film> getFilmByDirector(String keyWords) {
        return jdbcTemplate.query(SELECT_FILMS_WITH_KEY_WORD_BY_DIRECTOR_SQL, filmMapper, "%" + keyWords + "%");
    }

    @Override
    public Collection<Film> getFilmByNameOrDirector(String keyWords) {
        return jdbcTemplate.query(SELECT_FILMS_WITH_KEY_WORD_BY_DIRECTOR_AND_NAME_SQL, filmMapper, "%" + keyWords + "%", "%" + keyWords + "%");
    }


    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        return jdbcTemplate.query(SELECT_COMMON_FILMS_SQL, filmMapper, userId, friendId);
    }
}