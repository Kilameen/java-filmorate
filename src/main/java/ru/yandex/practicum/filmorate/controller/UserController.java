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
import ru.yandex.practicum.filmorate.service.user.UserService;
import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserService userService;

//Storage

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на список пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Запрос на получение фильма по id");
        return userService.getUserById(id);
    }

    @PostMapping
    @Validated(Marker.OnCreate.class) // Валидация для создания
    public User create(@Valid @RequestBody User user) {
        log.info("Создаем пользователя {}", user);
        User createUser = userService.create(user);
        log.info("Пользователь {} создан", user);
        return createUser;
    }

    @DeleteMapping
    public void deleteAllUser(User user) {
        log.info("Запрос на удаление всех фильмов");
        userService.deleteAllUser(user);
        log.info("Все фильмы удалены");
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class) // Валидация для обновления
    public User update(@Valid @RequestBody User updateUser) {
        log.info("Запрос на обновление информации о пользователе {}", updateUser);

        if (updateUser.getId() == null) {
            log.error("Id фильма не указан!");
            throw new ValidationException("Id фильма должен быть указан!");
        }
        try {
            log.info("Информация о пользователе {} обновлена!", updateUser);
            return userService.update(updateUser);
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