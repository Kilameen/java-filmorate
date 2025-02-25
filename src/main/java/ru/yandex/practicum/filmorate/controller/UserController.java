package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserStorage userStorage;

    @Autowired
    private final UserService userService;

//Storage

    @GetMapping
    public Collection<User> findAll() {
        Collection<User> userCollection = userStorage.findAll();
        log.debug("Список пользователей: {}", userCollection);
        return userCollection;
    }

    @PostMapping
    @Validated(Marker.OnCreate.class) // Валидация для создания
    public User create(@Valid @RequestBody User user) {
        log.info("Создаем пользователя {}", user);

        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Не указано имя пользователя. Приравниваем его к логину");
            user.setName(user.getLogin());
        }
        User createUser = userStorage.create(user);
        log.info("Пользователь {} создан", user);
        return createUser;
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class) // Валидация для обновления
    public User update(@Valid @RequestBody User updateUser) {
        if (updateUser.getId() == null) {
            log.error("Id пользователя не указан!");
            throw new ValidationException("Id пользователя должен быть указан!");
        }
        try {
            User userUpdate = userStorage.update(updateUser);
            log.info("Информация пользователя {} обновлена!", updateUser);
            log.debug(updateUser.toString());
            return userUpdate;
        } catch (NotFoundException e) {
            log.error("Пользователя с Id = {} не найдено.", updateUser.getId());
            throw new NotFoundException("Пользователя с Id = " + updateUser.getId() + " не найдено.");
        }
    }

    //Service

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long userFriendId) {
        log.info("Пользователь {} , хочет добавить в друзья {}.", userId, userFriendId);
        userService.addFriend(userId, userFriendId);
        log.info("{} и {}, теперь друзья!", userId, userFriendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable("id") Long userId) {
        log.info("Ваш список друзей");
        return userService.getFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getListOfMutualFriends(@PathVariable("id") Long userId, @PathVariable("otherId") Long userFriendId) {
        log.info("Cписок друзей, общих с пользователем {}.", userFriendId);
        return userService.getListOfMutualFriends(userId, userFriendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long userFriendId) {
        log.info("Пользователь {}, хочет удалить из друзей {}", userId, userFriendId);
        userService.deleteFriend(userId, userFriendId);
        log.info("Пользователь {} удален из вашего списка друзей.", userFriendId);
    }
}