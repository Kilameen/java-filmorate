package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    User getUserById(Long id);

    void deleteUserById(Long id);

    List<Event> getUserEvents(Long userId);
}