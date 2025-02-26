package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public User create(User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteAllUser(User user) {
        users.clear();
    }

    @Override
    public User deleteUser(Long id) {
        return users.remove(id);
    }

    @Override
    public User update(User updateUser) {
        if (updateUser.getId() == null) {
            throw new ValidationException("Id пользователя должен быть указан!");
        }
        validate(updateUser);
        if (users.containsKey(updateUser.getId())) {
            User oldUserInformation = users.get(updateUser.getId());
            oldUserInformation.setName(updateUser.getName());
            oldUserInformation.setLogin(updateUser.getLogin());
            oldUserInformation.setEmail(updateUser.getEmail());
            oldUserInformation.setBirthday(updateUser.getBirthday());
            return oldUserInformation;
        }
        throw new NotFoundException("Пользователя с Id = " + updateUser.getId() + " не найдено.");
    }

    private void validate(User user) throws DuplicatedDataException {
        if (users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), user.getId()))) {
            throw new DuplicatedDataException("Этот email уже используется");
        }

        if (users.values()
                .stream()
                .anyMatch(u -> u.getLogin().equals(user.getLogin()) && !Objects.equals(u.getId(), user.getId()))) {
            throw new DuplicatedDataException("Этот логин уже используется");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getEmail().contains(" ")) {
            throw new ValidationException("Email не может содержать пробелы");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}