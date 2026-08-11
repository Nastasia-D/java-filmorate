package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Primary
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEvent(Event event) {
        String sql = "INSERT INTO feed (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, event.getTimestamp(), event.getUserId(), event.getEventType().name(), event.getOperation().name(), event.getEntityId());
    }

    @Override
    public List<Event> getFeed(Long userId) {
        String sql = "SELECT * " +
                "FROM feed " +
                "WHERE user_id = ? " +
                "ORDER BY event_id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Event event = new Event();
            event.setEventId(rs.getLong("event_id"));
            event.setTimestamp(rs.getLong("timestamp"));
            event.setUserId(rs.getLong("user_id"));
            event.setEventType(EventType.valueOf(rs.getString("event_type")));
            event.setOperation(Operation.valueOf(rs.getString("operation")));
            event.setEntityId(rs.getLong("entity_id"));
            return event;
        }, userId);
    }
}
