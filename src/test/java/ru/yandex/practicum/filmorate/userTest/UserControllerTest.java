package ru.yandex.practicum.filmorate.userTest;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {

    private static Validator validator;

    @Autowired
    private UserController userController;

    User user;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        user = new User();
        user.setName("TestName");
        user.setLogin("TestLogin");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1993, 1, 25));
    }

    @AfterEach
    void setDown() {
        userController.deleteAllUser(user);
    }

    @Test
    void testCreateCorrectUser() {
        userController.create(user);
        Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Контроллер не создал пользователя");
        assertEquals("TestName", users.iterator().next().getName(), "Контроллер создал некорректое имя пользователя");
    }

    @Test
    void testUpdateUser() {
        User createdUser = userController.create(user);
        createdUser.setName("NewName");
        User updatedUser = userController.update(createdUser);
        assertEquals("NewName", updatedUser.getName(), "Контроллер не обновил имя пользователя");
    }

    @Test
    void testFindAllUsers() {
        userController.create(user);
        User user1 = new User();
        user1.setName("TestName2");
        user1.setLogin("TestLogin2");
        user1.setEmail("test2@yandex.ru");
        user1.setBirthday(LocalDate.of(1993, 1, 25));
        userController.create(user1);
        Collection<User> users = userController.findAll();
        assertEquals(2, users.size(), "Контроллер не создал пользователя");
    }

    @Test
    void testUserValidatesWrongEmail() {
        user.setEmail("test$yandex.ru");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Не пройдена валидация на email должен содержать символ @");
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        userController.create(user);
        User userWithSameEmail = new User();
        userWithSameEmail.setEmail("test@yandex.ru");
        userWithSameEmail.setLogin("anotherLogin");
        assertThrows(DuplicatedDataException.class, () -> userController.create(userWithSameEmail), "Создан дубликат с таким же email");
    }

    @Test
    void userValidatesBirthdayCannotBeInTheFuture() {
        user.setBirthday(LocalDate.of(2027, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class);
        assertEquals(1, violations.size(), "Не пройдена валидация, дата рождения не может быть в будущем");
    }

    @Test
    void testCreateUserWithDuplicateLogin() {
        userController.create(user);
        User userWithSameLogin = new User();
        userWithSameLogin.setEmail("another@yandex.ru");
        userWithSameLogin.setLogin("TestLogin");
        assertThrows(DuplicatedDataException.class, () -> userController.create(userWithSameLogin), "Создан дубликат с таким же логином");
    }

    @Test
    void testCreateUserLoginWithSpace() {
        user.setLogin("Login with space");
        assertThrows(ValidationException.class, () -> userController.create(user), "Логин добавлен с пробелами");
    }

    @Test
    void testCreateUserNameIsEmpty() {
        user.setName("");
        userController.create(user);
        assertEquals("TestLogin", user.getName(), "Пустое имя пользователя не было заменено на логин");
    }

    @Test
    void testCreateUserLoginIsEmpty() {
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class);
        assertEquals(1, violations.size(), "Не пройдена валидация на пустой логин");
    }

    @Test
    void testCreateUserEmailIsEmpty() {
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class);
        assertEquals(1, violations.size(), "Не пройдена валидация на пустой email");
    }

    @Test
    void testUserAddFriend() {
        userController.create(user);

        User user1 = new User();
        user1.setName("TestName1");
        user1.setLogin("TestLogin1");
        user1.setEmail("tests@yandex.ru");
        user1.setBirthday(LocalDate.of(1992, 1, 25));
        userController.create(user1);

        userController.addFriend(user.getId(), user1.getId());
        Collection<User> friends = userController.getFriends(user.getId());
        assertEquals(1, friends.size(), "Контроллер не добавил user в друзья user1");
        friends = userController.getFriends(user1.getId());
        assertEquals(1, friends.size(), "Контроллер не добавил user1 в друзья user");
    }

    @Test
    void testUserDeleteFriend() {
        userController.create(user);

        User user1 = new User();
        user1.setName("TestName1");
        user1.setLogin("TestLogin1");
        user1.setEmail("tests@yandex.ru");
        user1.setBirthday(LocalDate.of(1992, 1, 25));
        userController.create(user1);

        userController.addFriend(user.getId(), user1.getId());
        Collection<User> friends = userController.getFriends(user.getId());
        assertEquals(1, friends.size(), "Контроллер не добавил user в друзья user1");
        friends = userController.getFriends(user1.getId());
        assertEquals(1, friends.size(), "Контроллер не добавил user1 в друзья user");

        userController.deleteFriend(user.getId(), user1.getId());
        friends = userController.getFriends(user.getId());
        assertEquals(0, friends.size(), "Контроллер не удалил user1 из друзей user");
        friends = userController.getFriends(user1.getId());
        assertEquals(0, friends.size(), "Контроллер не удалил user из друзей user1");
    }

    @Test
    void testListOfMutualFriends() {
        userController.create(user);

        User user1 = new User();
        user1.setName("TestName1");
        user1.setLogin("TestLogin1");
        user1.setEmail("tests@yandex.ru");
        user1.setBirthday(LocalDate.of(1992, 1, 25));
        userController.create(user1);

        User user2 = new User();
        user2.setName("TestName2");
        user2.setLogin("TestLogin2");
        user2.setEmail("testy@yandex.ru");
        user2.setBirthday(LocalDate.of(1991, 1, 25));
        userController.create(user2);

        userController.addFriend(user.getId(), user1.getId());
        userController.addFriend(user.getId(), user2.getId());
        userController.addFriend(user1.getId(), user2.getId());

        Collection<User> friends = userController.getListOfMutualFriends(user.getId(), user1.getId());
        assertEquals(1, friends.size(), "Контроллер не нашел общих друзей");
        assertEquals("TestName2", friends.stream().findFirst().get().getName(), "Контроллер неверно нашел общих друзей");
    }
}