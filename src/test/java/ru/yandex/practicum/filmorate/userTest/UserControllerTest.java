package ru.yandex.practicum.filmorate.userTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = UserController.class)
public class UserControllerTest {

private static Validator validator;
private UserController userController;
private User user;

@BeforeEach
    void setUp(){
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    userController = new UserController();
    user = new User();
    user.setName("TestName");
    user.setLogin("TestLogin");
    user.setEmail("test@yandex.ru");
    user.setBirthday(LocalDate.of(1993, 1, 25));
}

@Test
    void testCreateCorrectUser(){
    userController.create(user);
    Collection<User>users = userController.findAll();
    assertEquals(1, users.size(), "Контроллер не создал пользователя");
    assertEquals("TestName", users.iterator().next().getName(), "Контроллер создал некорректое имя пользователя");
}

    @Test
    void testUpdateUser() {
        User createdUser = userController.create(user);
        createdUser.setName("NewName");
        User updatedUser = userController.update(createdUser);
        assertEquals("NewName", updatedUser.getName(),"Контроллер не обновил имя пользователя");
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
        assertEquals(2, users.size(),"Контроллер не создал пользователя");
    }
    @Test
    void testUserValidatesWrongEmail() {
        user.setEmail("test$yandex.ru");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Не пройдена валидация на email должен содержать символ @");
    }

    @Test
    void testCreateUserWithDuplicateEmailThrowsDuplicatedDataException() {
        userController.create(user);
        User userWithSameEmail = new User();
        userWithSameEmail.setEmail("test@yandex.ru");
        userWithSameEmail.setLogin("anotherLogin");
        assertThrows(DuplicatedDataException.class, () -> userController.create(userWithSameEmail),"Создан дубликат с таким же email");
    }

    @Test
    void userValidatesBirthdayCannotBeInTheFuture(){
    user.setBirthday(LocalDate.of(2027,1,1));
    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertEquals(1,violations.size(),"Не пройдена валидация, дата рождения не может быть в будущем");
    }

    @Test
    void testCreateUserWithDuplicateLoginThrowsDuplicatedDataException() {
        userController.create(user);
        User userWithSameLogin = new User();
        userWithSameLogin.setEmail("another@yandex.ru");
        userWithSameLogin.setLogin("TestLogin");
        assertThrows(DuplicatedDataException.class, () -> userController.create(userWithSameLogin),"Создан дубликат с таким же логином");
    }

    @Test
    void testCreateUserWithInvalidLoginThrowsValidationException() {
        user.setLogin("Login with space");
        assertThrows(ValidationException.class, () -> userController.create(user),"Логин добавлен с пробелами");
    }
}

