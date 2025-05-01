package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
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
}