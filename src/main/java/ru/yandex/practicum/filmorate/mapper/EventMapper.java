package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .timestamp(rs.getLong("timestamp"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}