package ru.yandex.practicum.filmorate.dao.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventDao {
    void create(Long userId, String eventType, String operation, Long entityId);

    List<Event> getUserEvents(Long userId);
}
