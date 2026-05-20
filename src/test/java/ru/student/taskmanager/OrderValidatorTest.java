package ru.student.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.student.taskmanager.model.Order;
import ru.student.taskmanager.model.OrderTypeValue;
import ru.student.taskmanager.service.OrderValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderValidatorTest {

    private OrderValidator validator;

    @BeforeEach
    void setUp() {
        validator = new OrderValidator();
    }

    @Test
    void validOrderHasNoErrors() {
        Order order = new Order("ORD-1", "Клиент", OrderTypeValue.REGULAR);

        assertTrue(validator.isValid(order));
        assertTrue(validator.validate(order).isEmpty());
    }

    @Test
    void nullOrderIsInvalid() {
        List<String> errors = validator.validate(null);

        assertFalse(validator.isValid(null));
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("null"));
    }

    @Test
    void emptyIdIsInvalid() {
        Order order = new Order("", "Клиент", OrderTypeValue.REGULAR);

        List<String> errors = validator.validate(order);

        assertFalse(validator.isValid(order));
        assertTrue(errors.stream().anyMatch(e -> e.contains("Идентификатор")));
    }

    @Test
    void blankCustomerNameIsInvalid() {
        Order order = new Order("ORD-2", "   ", OrderTypeValue.URGENT);

        List<String> errors = validator.validate(order);

        assertFalse(validator.isValid(order));
        assertTrue(errors.stream().anyMatch(e -> e.contains("Имя клиента")));
    }

    @Test
    void nullTypeIsInvalid() {
        Order order = new Order("ORD-3", "Клиент", null);

        List<String> errors = validator.validate(order);

        assertFalse(validator.isValid(order));
        assertTrue(errors.stream().anyMatch(e -> e.contains("Тип заказа")));
    }

    @Test
    void multipleErrorsForSeveralEmptyFields() {
        Order order = new Order(null, "", null);

        List<String> errors = validator.validate(order);

        assertEquals(3, errors.size());
    }
}
