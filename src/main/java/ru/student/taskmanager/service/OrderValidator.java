package ru.student.taskmanager.service;

import ru.student.taskmanager.annotation.Validate;
import ru.student.taskmanager.model.Order;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Проверяет заказ через Reflection по полям с аннотацией @Validate.
 */
public class OrderValidator {

    public boolean isValid(Order order) {
        return validate(order).isEmpty();
    }

    public List<String> validate(Order order) {
        List<String> errors = new ArrayList<>();

        if (order == null) {
            errors.add("Заказ не может быть null");
            return errors;
        }

        for (Field field : Order.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Validate.class)) {
                continue;
            }

            Validate annotation = field.getAnnotation(Validate.class);
            field.setAccessible(true);

            try {
                Object value = field.get(order);
                if (isEmpty(value)) {
                    errors.add(annotation.message());
                }
            } catch (IllegalAccessException e) {
                errors.add("Не удалось проверить поле: " + field.getName());
            }
        }

        return errors;
    }

    private boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String stringValue) {
            return stringValue.isBlank();
        }
        return false;
    }
}
