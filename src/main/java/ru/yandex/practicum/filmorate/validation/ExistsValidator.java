package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

@Component
@RequiredArgsConstructor
public class ExistsValidator implements ConstraintValidator<Exists, Long> {

    private final UserService userService;
    private final FilmService filmService;
    private Exists.EntityType entityType;

    @Override
    public void initialize(Exists constraintAnnotation) {
        this.entityType = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        boolean exists = false;
        String entityName = "";

        try {
            switch (entityType) {
                case USER:
                    exists = userService.findById(value).isPresent();
                    entityName = "Пользователь";
                    break;
                case FILM:
                    exists = filmService.findById(value).isPresent();
                    entityName = "Фильм";
                    break;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }

        if (!exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    entityName + " с id " + value + " не найден"
            ).addConstraintViolation();
        }

        return exists;
    }
}