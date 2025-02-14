package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
public class Film implements Marker {
    private Long id;

    @NotBlank(message = "Название не может быть пустым",
            groups = Marker.OnCreate.class)
    private String name;

    @Size(max = 200, message = "Длина названия не должна превышать 200 символов", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой", groups = Marker.OnCreate.class)
    private LocalDate releaseDate;

    @Positive(message = "Длительность не может быть отрицательной",
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private Integer duration;
}