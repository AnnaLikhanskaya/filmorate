package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private Instant timestamp;
    private Integer userId;
    private EventType eventType;
    private EventOperation operation;
    private Integer eventId;
    private Integer entityId;

    @JsonProperty("timestamp")
    public long getTimestampMillis() {
        return timestamp.toEpochMilli();
    }
}