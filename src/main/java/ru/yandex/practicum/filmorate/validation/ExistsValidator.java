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

        try {
            switch (entityType) {
                case USER:
                    userService.findById(value);
                    break;
                case FILM:
                    filmService.findById(value);
                    break;
                default:
                    return false;
            }
            return true;
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            String entityName = getEntityName();
            context.buildConstraintViolationWithTemplate(
                    entityName + " с id " + value + " не найден"
            ).addConstraintViolation();
            return false;
        }
    }

    private String getEntityName() {
        return switch (entityType) {
            case USER -> "Пользователь";
            case FILM -> "Фильм";
        };
    }
}