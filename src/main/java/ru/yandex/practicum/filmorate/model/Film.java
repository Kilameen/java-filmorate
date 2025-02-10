package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"name","description","releaseDate","duration"})
public class Film {
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
}
