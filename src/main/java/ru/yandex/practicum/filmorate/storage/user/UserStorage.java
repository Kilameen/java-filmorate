package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User getUserById(Long id);

    User create(User user);

    void deleteAllUser(User user);

    User deleteUser(Long id);

    User update(User updateUser);
}