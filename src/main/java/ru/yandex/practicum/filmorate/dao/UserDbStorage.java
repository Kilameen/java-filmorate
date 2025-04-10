package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserExistException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Reader;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

@Component(value = "H2UserDb")
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_REQUEST_DIRECTORY = "src/main/resources/requests/user/";
    private static final String INSERT_USER_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "addNewUser.sql");
    private static final String UPDATE_USER_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "updateUser.sql");
    private static final String SELECT_ALL_USERS_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getAllUsers.sql");
    private static final String SELECT_USER_BY_ID_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getUserById.sql");

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
        return jdbcTemplate.query(SELECT_ALL_USERS_SQL_REQUEST, new UserMapper());
    }

    @Override
    public User getUserById(Long id) {
        return jdbcTemplate.query(SELECT_USER_BY_ID_SQL_REQUEST, new UserMapper(), id).stream().findAny().orElse(null);
    }

    @Override
    public void deleteUser(User user) {
    }
}