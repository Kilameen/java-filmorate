package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
public class User implements Marker {
    Long id;

    @Email
    @NotBlank(message = "Email должен быть заполнен", groups = Marker.OnCreate.class)
    String email;

    @NotBlank(message = "Логин должен быть заполнен", groups = Marker.OnCreate.class)
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    LocalDate birthday;

    Set<Long> friends = new HashSet<>();
}