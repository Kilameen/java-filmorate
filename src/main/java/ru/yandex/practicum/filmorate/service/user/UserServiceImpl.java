package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import static java.util.Objects.isNull;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserServiceImpl(@Qualifier("H2UserDb") UserStorage userStorage, @Qualifier("H2FriendDb") FriendshipDao friendshipDao) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;
    }

    @Override
    public void addFriend(Long userId, Long userFriendId) {
        try {
            friendshipDao.addFriend(userId, userFriendId);

        } catch (Exception ex) {
            throw new NotFoundException("Ошибка поиска пользователя");
        }
    }

    @Override
    public void deleteFriend(Long userId, Long userFriendId) {
        getUserById(userId);
        getUserById(userFriendId);

        friendshipDao.deleteFriend(userId, userFriendId);
    }

    @Override
    public Collection<User> getMutualFriends(Long userId, Long userFriendId) {
        return friendshipDao.getMutualFriends(userId, userFriendId);
    }

    @Override
    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        validate(user);
        User updateUser = userStorage.update(user);
        if (isNull(updateUser)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        return updateUser;
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User getUserById(Long id) {
        User user = userStorage.getUserById(id);
        if (isNull(user)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        return user;
    }

    @Override
    public void deleteAllUsers(User user) {
        userStorage.deleteUser(user);
    }

    @Override
    public Collection<User> getAllUserFriends(Long id) {
        getUserById(id);
        return friendshipDao.getAllUserFriends(id);
    }

    private void validate(User user) {
        Collection<User> users = userStorage.findAll();

        if (users.stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Email {} уже используется", user.getEmail());
            throw new DuplicatedDataException("Этот email уже используется");
        }

        if (users.stream()
                .anyMatch(u -> u.getLogin().equals(user.getLogin()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Логин {} уже используется", user.getLogin());
            throw new DuplicatedDataException("Этот логин уже используется");
        }

        if (isNull(user.getLogin()) || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Login пуст, либо содержит в себе пробелы {}", user.getLogin());
            throw new ValidationException("Поле login должен быть заполнен и не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения позже сегодняшней даты {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (isNull(user.getName()) || user.getName().isEmpty()) {
            log.warn("Вместо пустого имени присваивается логин");
            user.setName(user.getLogin());
        }
    }
}