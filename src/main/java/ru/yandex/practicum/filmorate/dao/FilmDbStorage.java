package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.Reader;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

@Component(value = "H2FilmDb")
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_REQUEST_DIRECTORY = "src/main/resources/requests/film/";
    private static final String INSERT_FILM_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "addFilm.sql");
    private static final String UPDATE_FILM_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "updateFilm.sql");
    private static final String SELECT_ALL_FILM_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getAllFilms.sql");
    private static final String SELECT_FILM_BY_ID_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getFilmById.sql");
    private static final String SELECT_POPULAR_FILMS_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getPopularFilms.sql");

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
        Long filmId = keyHolder.getKey().longValue();
        // Устанавливаем ID в объект Film
        film.setId(filmId);
        // Возвращаем фильм с установленным ID
        return film;
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
        return jdbcTemplate.query(SELECT_ALL_FILM_SQL_REQUEST, new FilmMapper());
    }

    @Override
    public Film getFilm(Long id) {
        return jdbcTemplate.query(SELECT_FILM_BY_ID_SQL_REQUEST, new FilmMapper(), id).stream().findAny().orElse(null);
    }

    @Override
    public void deleteFilm(Film film) {
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return jdbcTemplate.query(SELECT_POPULAR_FILMS_SQL_REQUEST, new FilmMapper(), count);
    }
}