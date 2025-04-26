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

@Component(value = "H2FilmDb")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final DirectorStorage directorStorage;
    private final RatingDbStorage ratingDbStorage;

    private static final String INSERT_FILM_SQL = "INSERT INTO films (film_name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?);";
    private static final String UPDATE_FILM_SQL = "UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?;";
    private static final String SELECT_ALL_FILMS_SQL = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate, d.director_id, d.name AS director_name FROM films AS f LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id LEFT JOIN directors AS d ON fd.director_id = d.director_id GROUP BY f.film_id, r.rating_id, r.rating_name, d.director_id, d.name ORDER BY f.film_id;";
    private static final String SELECT_FILM_BY_ID_SQL = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate, d.director_id, d.name AS director_name FROM films AS f LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id LEFT JOIN directors AS d ON fd.director_id = d.director_id WHERE f.film_id = ? GROUP BY f.film_id, r.rating_id, r.rating_name, d.director_id, d.name;";
    private static final String SELECT_POPULAR_FILMS_SQL = "SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate, d.director_id, d.name AS director_name FROM films f LEFT JOIN rating_mpa r ON f.mpa_id = r.rating_id LEFT JOIN film_likes fl ON f.film_id = fl.film_id LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id LEFT JOIN directors AS d ON fd.director_id = d.director_id GROUP BY f.film_id, r.rating_id, r.rating_name ORDER BY rate DESC LIMIT ?;";
    private static final String SELECT_FILM_BY_DIRECTOR_SQL = "SELECT f.*,r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate,d.name,d.director_id FROM films AS f LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id LEFT JOIN directors AS d ON d.director_id = fd.director_id LEFT JOIN rating_mpa AS r ON f.mpa_id = r.rating_id LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id WHERE fd.director_id = ? GROUP BY f.film_id, r.rating_id, r.rating_name, d.director_id, d.name;";

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_FILM_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long filmId = keyHolder.getKeyAs(Long.class);
        film.setId(filmId);

        Rating mpa = ratingDbStorage.getRating(film.getMpa().getId());
        if (mpa == null) {
            throw new NotFoundException("Mpa c id:" + film.getMpa().getId() + " не найден");
        }
        film.setMpa(mpa);

        updateFilmDirectors(film);
        return getFilm(filmId);
    }

    private void updateFilmDirectors(Film film) {
        Long filmId = film.getId();
        Set<Director> directors = film.getDirectors();

        deleteAllDirectorsForFilm(filmId);
        saveFilmDirectors(filmId, directors);

        List<Director> filmDirectors = directorStorage.getFilmDirectors(filmId);
        film.setDirectors(new HashSet<>(filmDirectors));
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

    private void deleteAllDirectorsForFilm(Long filmId) {
        String sql = "DELETE FROM films_directors WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
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
        updateFilmDirectors(film);
        return getFilm(film.getId());
    }

    @Override
    public Collection<Film> findAll() {
        return jdbcTemplate.query(SELECT_ALL_FILMS_SQL, filmMapper);
    }

    @Override
    public Film getFilm(Long id) {
        Film film = jdbcTemplate.query(SELECT_FILM_BY_ID_SQL, filmMapper, id).stream().findFirst().orElse(null);

        if (film != null) {
            List<Director> filmDirectors = directorStorage.getFilmDirectors(id);
            film.setDirectors(new HashSet<>(filmDirectors));
        }
        return film;
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return jdbcTemplate.query(SELECT_POPULAR_FILMS_SQL, filmMapper, count);
    }

    @Override
    public List<Film> getDirectorFilms(Long directorId) {
        return jdbcTemplate.query(SELECT_FILM_BY_DIRECTOR_SQL, filmMapper, directorId);
    }
}
