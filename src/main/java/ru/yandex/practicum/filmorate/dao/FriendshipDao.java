package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface FriendshipDao {
    void addFriend(Long userIdOne, Long userIdTwo);

    void deleteFriend(Long userIdOne, Long userIdTwo);

    Boolean isFriend(Long userIdOne, Long userIdTwo);

    Collection<User> getAllUserFriends(Long userId);

    Collection<User> getMutualFriends(Long userIdOne, Long userIdTwo);
}
