package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsValidator.class)
public @interface Exists {
    String message() default "Объект не найден";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    EntityType value() default EntityType.USER;

    enum EntityType {
        USER, FILM
    }
}