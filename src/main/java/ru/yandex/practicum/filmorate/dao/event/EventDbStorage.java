package ru.yandex.practicum.filmorate.dao.event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.time.Instant;

@Component(value = "H2EventDb")
@RequiredArgsConstructor
public class EventDbStorage implements EventDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_EVENT_SQL_REQUEST = "INSERT INTO events (user_id, timestamp, event_type, operation, entity_id)" +
            "VALUES (?, ?, ?, ?, ?)";

    @Override
    public void create(Long userId, String eventType, String operation, Long entityId) {
        long timestamp = Timestamp.from(Instant.now()).getTime();
        jdbcTemplate.update(INSERT_EVENT_SQL_REQUEST, userId, timestamp, eventType, operation, entityId);
    }
}