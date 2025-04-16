package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    Long id;

    @Email
    @NotBlank(message = "Email должен быть заполнен", groups = Marker.OnCreate.class)
    String email;

    @NotBlank(message = "Логин должен быть заполнен", groups = Marker.OnCreate.class)
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    LocalDate birthday;
}