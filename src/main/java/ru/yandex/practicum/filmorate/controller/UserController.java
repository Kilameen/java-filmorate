package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Список фильмов: {}", users);
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user){
        log.info("Зарос на создание нового пользователя.");
        log.debug(user.toString());
        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Не указано имя пользователя. Приравниваем его к логину");
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(),user);
        log.info("Пользователь создан");
        log.debug(user.toString());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User updateUser){
        if(updateUser.getId() == null){
            log.error("Id пользователя не указан!");
            throw new ValidationException("Id пользователя должен быть указан!");
        }
        validate(updateUser);

if(users.containsKey(updateUser.getId())){
    User oldUserInformation = users.get(updateUser.getId());
    oldUserInformation.setName(updateUser.getName());
    oldUserInformation.setLogin(updateUser.getLogin());
    oldUserInformation.setEmail(updateUser.getEmail());
    oldUserInformation.setBirthday(updateUser.getBirthday());
    log.info("Информация пользователя обновлена!");
    log.debug(updateUser.toString());
    return oldUserInformation;
}
        log.error("Пользователя с Id = {} не найдено.", updateUser.getId());
        throw new NotFoundException("Пользователя с Id = " + updateUser.getId() + " не найдено.");
    }

    private void validate(User user) throws DuplicatedDataException {
        if (users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Email {} уже используется", user.getEmail());
            throw new DuplicatedDataException("Этот email уже используется");
        }

        if (users.values()
                .stream()
                .anyMatch(u -> u.getLogin().equals(user.getLogin()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Логин {} уже используется", user.getLogin());
            throw new DuplicatedDataException("Этот логин уже используется");
        }

        if (user.getLogin().contains(" ")) {
            log.error("Логин {} содержит пробелы", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
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
