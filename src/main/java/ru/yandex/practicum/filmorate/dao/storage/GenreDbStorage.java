package ru.yandex.practicum.filmorate.dao.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Component(value = "H2GenreDb")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;
    private static final String INSERT_GENRE_SQL_REQUEST = "INSERT INTO film_genres (film_id, genre_id)\n" +
            "VALUES (?, ?);";
    private static final String DELETE_GENRE_SQL_REQUEST = "DELETE\n" +
            "FROM film_genres\n" +
            "WHERE film_id = ?;";
    private static final String SELECT_ALL_GENRE_SQL_REQUEST = "SELECT *\n" +
            "FROM genres\n" +
            "ORDER BY genre_id;";
    private static final String SELECT_FILM_GENRE_SQL_REQUEST = "SELECT genres.genre_id, genre_name\n" +
            "FROM film_genres\n" +
            "LEFT JOIN genres ON film_genres.genre_id = genres.genre_id\n" +
            "WHERE film_id = ?\n" +
            "ORDER BY genres.genre_id";
    private static final String SELECT_GENRE_BY_ID_SQL_REQUEST = "SELECT *\n" +
            "FROM genres\n" +
            "WHERE genre_id = ?;";
    private static final String SELECT_GENRES_ALL_FILMS_SQL_REQUEST = "SELECT fg.film_id, g.*\n" +
            "FROM film_genres fg\n" +
            "JOIN genres g ON fg.genre_id = g.genre_id\n" +
            "WHERE fg.film_id IN (:filmIds);";

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        return jdbcTemplate.query(SELECT_FILM_GENRE_SQL_REQUEST, genreMapper, filmId);
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
                result.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
            }
            return result;
        });
    }

    @Override
    public void setGenres(Long filmId, List<Long> genreIds) {
        Set<Long> uniqueGenreIds = new HashSet<>(genreIds);

        List<Object[]> batchArgs = uniqueGenreIds.stream()
                .map(genreId -> new Object[]{filmId, genreId})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(INSERT_GENRE_SQL_REQUEST, batchArgs);
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query(SELECT_ALL_GENRE_SQL_REQUEST, genreMapper);
    }

    @Override
    public Genre getGenre(Long genreId) {
        return jdbcTemplate.query(SELECT_GENRE_BY_ID_SQL_REQUEST, genreMapper, genreId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Жанр с id " + genreId + " не найден"));
    }

    @Override
    public void clearFilmGenres(Long filmId) {
        jdbcTemplate.update(DELETE_GENRE_SQL_REQUEST, filmId);
    }
}