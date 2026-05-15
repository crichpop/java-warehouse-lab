package ru.student.taskmanager;

import org.junit.jupiter.api.Test;
import ru.student.taskmanager.model.Order;
import ru.student.taskmanager.model.OrderTypeValue;
import ru.student.taskmanager.service.OrderProducer;

import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderProducerTest {

    @Test
    void createOrderReturnsOrderWithFields() {
        OrderProducer producer = new OrderProducer(new LinkedBlockingQueue<>(), 1);
        Order order = producer.createOrder("ORD-1", "Иван", OrderTypeValue.URGENT);

        assertEquals("ORD-1", order.getId());
        assertEquals("Иван", order.getCustomerName());
        assertEquals(OrderTypeValue.URGENT, order.getType());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void createOrderForIndexMarksEveryThirdAsUrgent() {
        OrderProducer producer = new OrderProducer(new LinkedBlockingQueue<>(), 10);

        assertEquals(OrderTypeValue.URGENT, producer.createOrderForIndex(0).getType());
        assertEquals(OrderTypeValue.REGULAR, producer.createOrderForIndex(1).getType());
        assertEquals(OrderTypeValue.REGULAR, producer.createOrderForIndex(2).getType());
        assertEquals(OrderTypeValue.URGENT, producer.createOrderForIndex(3).getType());
    }
}
