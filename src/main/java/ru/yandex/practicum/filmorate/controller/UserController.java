package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    private final UserService userService;

//Storage

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на список пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя по id");
        return userService.getUserById(id);
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.info("Создаем пользователя {}", user);
        User createUser = userService.create(user);
        log.info("Пользователь {} создан", user);
        return createUser;
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class) // Валидация для обновления
    public User update(@Valid @RequestBody User updateUser) {
        log.info("Запрос на обновление информации о пользователе {}", updateUser);
        return userService.update(updateUser);
    }

    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя с id {}", userId);
        userService.deleteUserById(userId);
        log.info("Пользователь с id {} удален", userId);
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
        return userService.getAllUserFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getListOfMutualFriends(@PathVariable("id") Long userId, @PathVariable("otherId") Long userFriendId) {
        log.info("Cписок друзей, общих с пользователем {}.", userFriendId);
        return userService.getMutualFriends(userId, userFriendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long userFriendId) {
        log.info("Пользователь {}, хочет удалить из друзей {}", userId, userFriendId);
        userService.deleteFriend(userId, userFriendId);
        log.info("Пользователь {} удален из вашего списка друзей.", userFriendId);
    }
}