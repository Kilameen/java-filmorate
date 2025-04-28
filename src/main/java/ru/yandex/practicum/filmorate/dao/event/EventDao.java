package ru.yandex.practicum.filmorate.dao.event;

public interface EventDao {
    void create(Long userId, String eventType, String operation, Long entityId);
}
