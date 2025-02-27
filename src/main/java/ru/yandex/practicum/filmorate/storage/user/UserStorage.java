package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User getUserById(Long id);

    User create(User user) throws MethodArgumentNotValidException;

    void deleteAllUser(User user);

    User deleteUser(Long id);

    User update(User updateUser) throws MethodArgumentNotValidException;
}