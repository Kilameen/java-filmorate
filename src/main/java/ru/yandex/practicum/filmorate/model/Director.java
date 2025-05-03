package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Director {
    private Long id;
    @NotEmpty (groups = {Marker.OnCreate.class})
    private String name;
}
