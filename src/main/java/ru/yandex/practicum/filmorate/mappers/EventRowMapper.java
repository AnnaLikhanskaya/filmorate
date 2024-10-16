package ru.yandex.practicum.filmorate.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Slf4j
@Component
public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        EventType eventType;
        EventOperation operation;
        try {
            eventType = EventType.valueOf(rs.getString("event_type"));
            operation = EventOperation.valueOf(rs.getString("operation"));
        } catch (IllegalArgumentException e) {
            log.error("Invalid enum value for event_type or operation in the database: event_type={}, operation={}",
                    rs.getString("event_type"), rs.getString("operation"));
            throw new SQLException("Invalid enum value in database", e);
        }
        return Event.builder()
                .eventId(rs.getInt("id"))
                .timestamp(Instant.ofEpochMilli(rs.getLong("event_timestamp")))
                .userId(rs.getInt("user_id"))
                .eventType(eventType)
                .operation(operation)
                .entityId(rs.getInt("entity_id"))
                .build();
    }
}