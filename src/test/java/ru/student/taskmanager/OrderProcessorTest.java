package ru.student.taskmanager;

import org.junit.jupiter.api.Test;
import ru.student.taskmanager.model.Order;
import ru.student.taskmanager.model.OrderStatus;
import ru.student.taskmanager.model.OrderTypeValue;
import ru.student.taskmanager.service.OrderProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderProcessorTest {

    @Test
    void processSetsProcessedStatusAndTime() throws InterruptedException {
        OrderProcessor processor = new OrderProcessor(5, 10);
        Order order = new Order("ORD-1", "Клиент", OrderTypeValue.REGULAR);

        processor.process(order);

        assertEquals(OrderStatus.PROCESSED, order.getStatus());
        assertNotNull(order.getProcessedAt());
    }
}
