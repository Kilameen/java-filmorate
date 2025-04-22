package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    Long id;
    Long user_id;
    Long film_id;
    boolean isPositive;
    @Size(max = 200, message = "Длина отзыва не должна превышать 200 символов",
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    String content;
    int useful;
}
