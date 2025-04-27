package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserExistException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

@Component(value = "H2UserDb")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    private static final String INSERT_USER_SQL_REQUEST = "INSERT INTO users (email, login, user_name, birthday) VALUES (?, ?, ?, ?);";
    private static final String UPDATE_USER_SQL_REQUEST = "UPDATE users\n" +
            "SET email = ?,\n" +
            "    login = ?,\n" +
            "    user_name = ?,\n" +
            "    birthday = ?\n" +
            "WHERE user_id = ?;";
    private static final String SELECT_ALL_USERS_SQL_REQUEST = "SELECT *\n" +
            "FROM users;";
    private static final String SELECT_USER_BY_ID_SQL_REQUEST = "SELECT *\n" +
            "FROM users\n" +
            "WHERE user_id = ?;";
    private static final String DELETE_USER_BY_ID_SQL_REQUEST = "DELETE FROM users WHERE user_id = ?;";
    private static final String DELETE_USER_FRIENDSHIPS_SQL =
            "DELETE FROM friendship WHERE user_id = ? OR friend_id = ?;";


    @Override
    public User create(User user) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection
                        .prepareStatement(INSERT_USER_SQL_REQUEST,
                                Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getLogin());
                preparedStatement.setString(3, user.getName());
                preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
                return preparedStatement;
            }, keyHolder);

            Long userId = keyHolder.getKey().longValue();
            user.setId(userId);
            return user;
        } catch (Exception e) {
            if (e.getMessage().contains("email")) {
                throw new UserExistException("email", user.getEmail());
            } else if (e.getMessage().contains("login")) {
                throw new UserExistException("login", user.getLogin());
            } else {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update(UPDATE_USER_SQL_REQUEST,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        return getUserById(user.getId());
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query(SELECT_ALL_USERS_SQL_REQUEST, userMapper);
    }

    @Override
    public User getUserById(Long id) {
        return jdbcTemplate.query(SELECT_USER_BY_ID_SQL_REQUEST, userMapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public void deleteUserById(Long id) {
        jdbcTemplate.update(DELETE_USER_FRIENDSHIPS_SQL, id, id);

        jdbcTemplate.update(DELETE_USER_BY_ID_SQL_REQUEST, id);
    }
}