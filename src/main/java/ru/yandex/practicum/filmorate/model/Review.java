package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    Long reviewId;
    @NotNull(message = "Укажите id пользователя, оставившего отзыв")
    Long userId;
    @NotNull(message = "Укажите id фильма, которому оставляете отзыв")
    Long filmId;
    @NotNull(message = "Тип отзыва обязателен")
    Boolean isPositive;
    @Size(max = 255, message = "Длина отзыва не должна превышать 255 символов",
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    String content;
    int useful;
}
