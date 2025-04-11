package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.utils.Reader;
import java.util.*;

@Component
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_REQUEST_DIRECTORY = "src/main/resources/requests/genre/";
    private static final String INSERT_GENRE_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "addGenre.sql");
    private static final String DELETE_GENRE_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "deleteGenre.sql");
    private static final String SELECT_ALL_GENRE_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getAllGenre.sql");
    private static final String SELECT_FILM_GENRE_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getFilmGenre.sql");
    private static final String SELECT_GENRE_BY_ID_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getGenreById.sql");
    private static final String SELECT_GENRES_ALL_FILMS_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getGenresAllFilms.sql");

    @Override
    public Collection<Genre> getFilmGenres(Long filmId) {
        return jdbcTemplate.query(SELECT_FILM_GENRE_SQL_REQUEST, new GenreMapper(), filmId);
    }

    @Override
    public Map<Long, Collection<Genre>> getAllFilmsGenres(Collection<Long> filmIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmIds", filmIds);
        return namedParameterJdbcTemplate.query(SELECT_GENRES_ALL_FILMS_SQL_REQUEST, parameters, rs -> {
            Map<Long, Collection<Genre>> result = new HashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("film_id");
                Genre genre = new Genre(
                        rs.getLong("genre_id"),
                        rs.getString("genre_name")
                );
                result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
            }
            return result;
        });
    }

    @Override
    public void setGenres(Long filmId, Long genreId) {
        jdbcTemplate.update(INSERT_GENRE_SQL_REQUEST, filmId, genreId);
    }

    @Override
    public Collection<Genre> getGenres() {
        return new ArrayList<>(jdbcTemplate.query(SELECT_ALL_GENRE_SQL_REQUEST, new GenreMapper()));
    }

    @Override
    public Genre getGenre(Long genreId) {
        return jdbcTemplate.query(SELECT_GENRE_BY_ID_SQL_REQUEST, new GenreMapper(), genreId)
                .stream().findAny().orElse(null);
    }

    @Override
    public void clearFilmGenres(Long filmId) {
        jdbcTemplate.update(DELETE_GENRE_SQL_REQUEST, filmId);
    }
}