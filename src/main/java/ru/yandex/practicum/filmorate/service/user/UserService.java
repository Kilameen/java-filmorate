package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;

public interface UserService {
    void addFriend(Long user, Long userFriend);

    void deleteFriend(Long user, Long userFriend);

    Collection<User> getMutualFriends(Long user, Long userFriend);

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    User getUserById(Long id);

    Collection<User> getAllUserFriends(Long id);

    void deleteUserById(Long id);

    Collection<Film> getRecommendation(Long id);

    List<Event> getUserEvents(Long userId);
}