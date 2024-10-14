package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private EventOperation operation;
    private Integer eventId;
    private Integer entityId;
}