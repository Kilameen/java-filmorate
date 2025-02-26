package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    void addFriend(Long userId, Long userFriendId);

    void deleteFriend(Long userId, Long userFriendId);

    Collection<User> getFriends(Long userId);

    Collection<User> getListOfMutualFriends(Long userId, Long userFriendId);

    User create(User user);

    User update(User updateUser);

    Collection<User> findAll();

    User getUserById(Long id);

    void deleteAllUser(User user);
}
