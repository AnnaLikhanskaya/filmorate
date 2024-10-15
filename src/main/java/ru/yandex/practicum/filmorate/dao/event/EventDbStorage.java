package ru.yandex.practicum.filmorate.dao.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
public class EventDbStorage extends BaseRepository<Event> implements EventStorage {
    public EventDbStorage(JdbcTemplate jdbc, EventRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addEvent(Event event) {
        super.insert("INSERT INTO EVENTS (event_timestamp, user_id, event_type, operation, entity_id)" +
                        " VALUES (?, ?, ?, ?, ?);",
                event.getTimestamp().toEpochMilli(),
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId()
        );
    }

    @Override
    public List<Event> getEvents(Integer id) {
        return super.findMany("SELECT * FROM EVENTS WHERE user_id = ?", id);
    }
}