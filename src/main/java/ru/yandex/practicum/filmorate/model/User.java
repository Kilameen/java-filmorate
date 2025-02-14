package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import java.time.LocalDate;

@Data
public class User implements Marker {
    private Long id;

    @Email
    @NotBlank(message = "Email должен быть заполнен", groups = Marker.OnCreate.class)
    private String email;

    @NotBlank(message = "Логин должен быть заполнен", groups = Marker.OnCreate.class)
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private LocalDate birthday;
}