package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        log.info("Запрос на получение всех режиссёров");
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable Long id) {
        log.info("Запрос на получение режиссёра с id: {}", id);
        return directorService.findById(id);
    }

    @PostMapping
    public Director create(@RequestBody Director director) {
        log.info("Запрос на создание режиссёра: {}", director);
        validateDirector(director);
        Director created = directorService.create(director);
        log.info("Режиссёр создан: {}", created);
        return created;
    }

    @PutMapping
    public Director update(@RequestBody Director director) {
        log.info("Запрос на обновление режиссёра: {}", director);

        validateDirector(director);

        if (director.getId() == null) {
            log.warn("Попытка обновления режиссёра без ID");
            throw new ValidationException("ID режиссёра не может быть пустым");
        }

        Director updated = directorService.update(director);
        log.info("Режиссёр обновлён: {}", updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Запрос на удаление режиссёра с id: {}", id);
        directorService.delete(id);
        log.info("Режиссёр с id {} удалён", id);
    }

    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            log.warn("Ошибка валидации: имя режиссёра пустое");
            throw new ValidationException("Имя режиссёра не может быть пустым");
        }
    }
}