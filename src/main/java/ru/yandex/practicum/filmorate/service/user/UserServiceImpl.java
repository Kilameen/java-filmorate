package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public void addFriend(Long userId, Long userFriendId) {
        if (Objects.equals(userId, userFriendId)) {
            throw new ValidationException("Id пользователей не могут быть равны");
        }
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(userFriendId);
        user.getFriends().add(userFriend.getId());
        userFriend.getFriends().add(user.getId());
    }

    @Override
    public void deleteFriend(Long userId, Long userFriendId) {
        if (Objects.equals(userId, userFriendId)) {
            throw new ValidationException("Id пользователей не могут быть равны");
        }
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(userFriendId);
        user.getFriends().remove(userFriend.getId());
        userFriend.getFriends().remove(user.getId());
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        return user.getFriends()
                .stream()
                .map(userStorage::getUserById)
                .toList();
    }

    @Override
    public Collection<User> getListOfMutualFriends(Long userId, Long userFriendId) {
        if (Objects.equals(userId, userFriendId)) {
            throw new ValidationException("Id пользователей не могут быть равны");
        }
        Set<Long> friendUser1 = userStorage.getUserById(userId).getFriends();
        Set<Long> friendUser2 = userStorage.getUserById(userFriendId).getFriends();
        return friendUser1
                .stream()
                .filter(friendUser2::contains)
                .map(userStorage::getUserById)
                .toList();
    }

    @Override
    public User create(User user) throws MethodArgumentNotValidException {
        return userStorage.create(user);
    }

    @Override
    public User update(User updateUser) throws MethodArgumentNotValidException {
        return userStorage.update(updateUser);
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    @Override
    public void deleteAllUser(User user) {
        userStorage.deleteAllUser(user);
    }
}