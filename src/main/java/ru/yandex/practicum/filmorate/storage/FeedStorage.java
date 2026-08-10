package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {

    void addEvent(Event event);
    List<Event> getFeed(Long userId);
}
