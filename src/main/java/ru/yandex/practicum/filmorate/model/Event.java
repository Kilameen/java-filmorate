package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    Long eventId;
    @NotNull(message = "Id пользователя совершившего событие не может быть пустым")
    Long userId;
    @NotNull(message = "Время когда случилось событие не может быть пустым")
    Long timestamp;
    String eventType;
    String operation;
    @NotNull(message = "Id пользователя с которым произошло событие не может быть пустым")
    Long entityId;
}