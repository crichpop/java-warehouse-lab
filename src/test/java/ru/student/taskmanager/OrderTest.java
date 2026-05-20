package ru.student.taskmanager;

import org.junit.jupiter.api.Test;
import ru.student.taskmanager.model.Order;
import ru.student.taskmanager.model.OrderStatus;
import ru.student.taskmanager.model.OrderTypeValue;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    @Test
    void defaultConstructorSetsCreatedStatusAndCreatedAt() {
        Order order = new Order();

        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void parameterizedConstructorSetsFields() {
        Order order = new Order("ORD-1", "Иван", OrderTypeValue.URGENT);

        assertEquals("ORD-1", order.getId());
        assertEquals("Иван", order.getCustomerName());
        assertEquals(OrderTypeValue.URGENT, order.getType());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void settersAndGettersWork() {
        Order order = new Order();
        LocalDateTime now = LocalDateTime.now();

        order.setId("ORD-2");
        order.setCustomerName("Пётр");
        order.setType(OrderTypeValue.REGULAR);
        order.setStatus(OrderStatus.PROCESSING);
        order.setProcessedAt(now);

        assertEquals("ORD-2", order.getId());
        assertEquals("Пётр", order.getCustomerName());
        assertEquals(OrderTypeValue.REGULAR, order.getType());
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        assertEquals(now, order.getProcessedAt());
    }

    @Test
    void toStringContainsMainFields() {
        Order order = new Order("ORD-3", "Анна", OrderTypeValue.REGULAR);

        String text = order.toString();

        assertTrue(text.contains("ORD-3"));
        assertTrue(text.contains("Анна"));
        assertTrue(text.contains("REGULAR"));
    }

    @Test
    void equalsBasedOnId() {
        Order first = new Order("ORD-4", "Клиент", OrderTypeValue.URGENT);
        Order second = new Order("ORD-4", "Другой", OrderTypeValue.REGULAR);
        Order third = new Order("ORD-5", "Клиент", OrderTypeValue.URGENT);

        assertEquals(first, second);
        assertNotEquals(first, third);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
