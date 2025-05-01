package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component(value = "H2LikeDb")
@RequiredArgsConstructor
public class LikeDbStorage implements LikeDao {

    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_LIKE_SQL_REQUEST = "INSERT INTO film_likes (film_id, user_id)\n" +
            "VALUES (?, ?);";
    private static final String DELETE_LIKE_SQL_REQUEST = "DELETE\n" +
            "FROM film_likes\n" +
            "WHERE film_id = ? AND user_id = ?;";
    private static final String SELECT_ALL_USERS_AND_LIKES =
            "SELECT user_id, film_id FROM film_likes";

    @Override
    public void addLike(Long filmId, Long userId) {
        if(hasLike(filmId,userId)){
            return;
        }
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

    @Override
    public Map<Long, Set<Long>> getAllUsersWithLikes() {
        Map<Long, Set<Long>> usersWithLikes = new HashMap<>();

        jdbcTemplate.query(SELECT_ALL_USERS_AND_LIKES, rs -> {
            Long userId = rs.getLong("user_id");
            Long filmId = rs.getLong("film_id");

            usersWithLikes.computeIfAbsent(userId, k -> new HashSet<>());
            usersWithLikes.get(userId).add(filmId);
        });
        return usersWithLikes;
    }

    private List<Long> getUserLikes(Long userId) {
        String sql = "SELECT film_id FROM film_likes WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }

    // Проверка:
    private boolean hasLike(Long filmId, Long userId) {
        List<Long> userLikes = getUserLikes(userId);
        return userLikes != null && userLikes.contains(filmId);
    }
}