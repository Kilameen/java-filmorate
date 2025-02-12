package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(exclude = {"email", "login", "name", "birthday"})
public class User {
    private Long id;

    @Email
    @NotNull(message = "Email должен быть заполнен")
    @NotBlank(message = "Email должен быть заполнен")
    private String email;

    @NotBlank(message = "Логин должен быть заполнен")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}