package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<Director> findAll() {
        log.info("Получение всех режиссёров");
        return directorStorage.findAll();
    }

    public Director findById(Long id) {
        log.info("Поиск режиссёра с id: {}", id);
        return directorStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Режиссёр с id " + id + " не найден"));
    }

    public Director create(Director director) {
        log.info("Создание режиссёра: {}", director);
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        log.info("Обновление режиссёра: {}", director);
        findById(director.getId());
        return directorStorage.update(director);
    }

    public void delete(Long id) {
        log.info("Удаление режиссёра с id: {}", id);
        findById(id);
        directorStorage.delete(id);
        log.info("Режиссёр с id {} успешно удалён", id);
    }
}