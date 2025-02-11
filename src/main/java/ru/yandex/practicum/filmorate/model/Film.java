package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@EqualsAndHashCode(exclude = {"name", "description", "releaseDate", "duration"})
public class Film {
    private Long id;

    @NotNull(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Длина названия не должна превышать 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Длительность не может быть отрицательной")
    private Integer duration;
}
