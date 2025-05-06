package ru.yandex.practicum.filmorate.dao.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DirectorMapper mapper;

    private static final String INSERT_INTO_QUERY = "INSERT INTO directors(name) VALUES(?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE director_id = ?";
    private static final String GET_ALL_DIRECTORS = "SELECT * FROM directors ORDER BY director_id";
    private static final String GET_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?";
    private static final String GET_FILM_DIRECTOR_BY_ID =
            "SELECT d.director_id, d.name " +
            "FROM directors AS d " +
            "LEFT JOIN films_directors AS fd ON d.director_id = fd.director_id " +
            "WHERE film_id = ?";
    private static final String SELECT_DIRECTORS_ALL_FILMS_SQL_REQUEST = "SELECT fd.film_id,\n" +
            "d.director_id,\n" +
            "d.name\n" +
            "FROM films_directors AS fd\n" +
            "INNER JOIN directors AS d ON fd.director_id = d.director_id\n" +
            "WHERE fd.film_id IN (:filmIds);\n";

    @Override
    public List<Director> getDirectors() {
        return jdbcTemplate.query(GET_ALL_DIRECTORS, mapper);
    }

    @Override
    public Optional<Director> getDirectorById(Long directorId) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(GET_DIRECTOR_BY_ID, mapper, directorId));
    }

    @Override
    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_INTO_QUERY,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, director.getName());
            return preparedStatement;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director newDirector) {
        int rowsUpdated = jdbcTemplate.update(UPDATE_QUERY, newDirector.getName(), newDirector.getId());
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные для режиссера с ID " + newDirector.getId());
        }
        return getDirectorById(newDirector.getId())
                .orElseThrow(() -> new InternalServerException("Ошибка при обновлении режиссера"));
    }

    @Override
    public void deleteDirector(Long directorId) {

        int rowUpdated = jdbcTemplate.update(DELETE_QUERY, directorId);
        if (rowUpdated == 0) {
            throw new NotFoundException("Режиссер с id " + directorId + " не найден и не был удалён.");
        }
    }

    @Override
    public List<Director> getFilmDirectors(Long filmId) {
        return jdbcTemplate.query(GET_FILM_DIRECTOR_BY_ID, mapper, filmId);
    }

    @Override
    public Map<Long, Collection<Director>> getAllFilmsDirectors(Collection<Long> filmIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmIds", filmIds);
        return namedParameterJdbcTemplate.query(SELECT_DIRECTORS_ALL_FILMS_SQL_REQUEST, parameters, rs -> {
            Map<Long, Collection<Director>> result = new HashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("film_id");
                Director director = new Director(
                        rs.getLong("director_id"),
                        rs.getString("name")
                );
                result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(director);
            }
            return result;
        });
    }

    @Override
    public void updateFilmDirectors(Film film) {
        Long filmId = film.getId();
        Collection<Director> directors = film.getDirectors();

        deleteAllDirectorsForFilm(filmId);
        saveFilmDirectors(filmId, directors);

        List<Director> filmDirectors = getFilmDirectors(filmId);
        film.setDirectors(new HashSet<>(filmDirectors));
    }

    private void saveFilmDirectors(Long filmId, Collection<Director> directors) {
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
}