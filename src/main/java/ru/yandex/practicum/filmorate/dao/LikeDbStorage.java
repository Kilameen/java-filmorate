package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
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
    private static final String SELECT_FILM_LIKES = " SELECT *\n" +
            "FROM film_likes\n WHERE film_id?;";
    private static final String SELECT_ALL_USERS_LIKES = "SELECT user_id\n" +
            "FROM film_likes\n GROUP BY user_id;";
    private static final String SELECT_ALL_FILMS_LIKES = "SELECT film_id\n" +
            "FROM film_likes\n WHERE user_id = ?";

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

    @Override
    public Collection<Long> getFilmLikes(Film film) {
        return jdbcTemplate.query(SELECT_FILM_LIKES, (rs, rowNum) -> rs.getLong("user_id"), film.getId());
    }

    @Override
    public Map<Long, Set<Long>> getAllUsersWithLikes() {
        Map<Long, Set<Long>> usersWithLikes = new HashMap<>();
        Collection<Long> users = jdbcTemplate.query(SELECT_ALL_USERS_LIKES, (rs, rowNum) -> rs.getLong("user_id"));
        for (Long user : users) {
            Collection<Long> likes = jdbcTemplate.query(SELECT_ALL_FILMS_LIKES, (rs, rowNum) -> rs.getLong("film_id"), user);
            usersWithLikes.put(user, new HashSet<>(likes));
        }
        return usersWithLikes;
    }
}