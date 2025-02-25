package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserStorage userStorage;

    public void addFriend(Long userId, Long userFriendId) {
        if (Objects.equals(userId, userFriendId)) {
            throw new ValidationException("Id пользователей не могут быть равны");
        }
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(userFriendId);
        user.getFriends().add(userFriend.getId());
        userFriend.getFriends().add(user.getId());
    }

    public void deleteFriend(Long userId, Long userFriendId) {
        if (Objects.equals(userId, userFriendId)) {
            throw new ValidationException("Id пользователей не могут быть равны");
        }
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(userFriendId);
        user.getFriends().remove(userFriend.getId());
        userFriend.getFriends().remove(user.getId());
    }

    public Collection<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        return user.getFriends()
                .stream()
                .map(userStorage::getUserById)
                .toList();
    }

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
}