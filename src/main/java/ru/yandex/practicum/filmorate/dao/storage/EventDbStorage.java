package ru.yandex.practicum.filmorate.dao.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component(value = "H2EventDb")
@RequiredArgsConstructor
public class EventDbStorage implements EventDao {

    private final JdbcTemplate jdbcTemplate;
    private final EventMapper eventMapper;

    private static final String INSERT_EVENT_SQL_REQUEST = "INSERT INTO events (user_id, timestamp, event_type, operation, entity_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_EVENT_SQL_REQUEST = "SELECT * FROM events WHERE user_id = ? ORDER BY event_id";

    @Override
    public void create(Long userId, String eventType, String operation, Long entityId) {
        long timestamp = Timestamp.from(Instant.now()).getTime();
        jdbcTemplate.update(INSERT_EVENT_SQL_REQUEST, userId, timestamp, eventType, operation, entityId);
    }

    @Override
    public List<Event> getUserEvents(Long userId) {
        return jdbcTemplate.query(SELECT_EVENT_SQL_REQUEST, eventMapper, userId);
    }
}