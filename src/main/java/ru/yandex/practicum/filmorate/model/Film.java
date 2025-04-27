package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    Long id;

    @NotBlank(message = "Название не может быть пустым",
            groups = Marker.OnCreate.class)
    String name;

    @Size(max = 200, message = "Длина названия не должна превышать 200 символов", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    String description;

    @NotNull(message = "Дата релиза не может быть пустой", groups = Marker.OnCreate.class)
    LocalDate releaseDate;

    @Positive(message = "Длительность не может быть отрицательной",
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    Integer duration;

    Long likes;

    @NotNull(message = "Рейтинг MPA не может быть пустым", groups = Marker.OnCreate.class)
    Rating mpa;
    Collection<Genre> genres = new HashSet<>();

    @NotNull
    Set<Director> directors = new HashSet<>();
}