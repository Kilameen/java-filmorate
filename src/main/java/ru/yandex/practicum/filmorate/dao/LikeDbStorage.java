package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.utils.Reader;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeDao{

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_REQUEST_DIRECTORY = "src/main/resources/requests/film/like/";
    private static final String INSERT_LIKE_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "addLikeTheFilm.sql");
    private static final String DELETE_LIKE_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "deleteLikeTheFilm.sql");

    @Override
    public void addLike(Long filmId, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_LIKE_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, filmId);
            preparedStatement.setLong(2, userId);
            return preparedStatement;
        }, keyHolder);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(DELETE_LIKE_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, filmId);
            preparedStatement.setLong(2, userId);
            return preparedStatement;
        }, keyHolder);
    }
}