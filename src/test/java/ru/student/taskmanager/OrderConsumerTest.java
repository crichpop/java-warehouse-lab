package ru.student.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.student.taskmanager.model.Order;
import ru.student.taskmanager.model.OrderStatus;
import ru.student.taskmanager.model.OrderTypeValue;
import ru.student.taskmanager.service.OrderConsumer;
import ru.student.taskmanager.service.OrderProcessor;
import ru.student.taskmanager.service.OrderValidator;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderConsumerTest {

    private BlockingQueue<Order> queue;
    private Map<String, Order> processedOrders;
    private OrderConsumer consumer;
    private AtomicBoolean running;

    @BeforeEach
    void setUp() {
        queue = new LinkedBlockingQueue<>();
        processedOrders = new ConcurrentHashMap<>();
        running = new AtomicBoolean(true);
        consumer = new OrderConsumer(
                queue,
                processedOrders,
                new OrderValidator(),
                new OrderProcessor(10, 20),
                running
        );
    }

    @Test
    void handleOrderProcessesValidOrder() throws InterruptedException {
        Order order = new Order("ORD-1", "Клиент", OrderTypeValue.REGULAR);

        consumer.handleOrder(order, "test-thread");

        assertEquals(OrderStatus.PROCESSED, order.getStatus());
        assertNotNull(order.getProcessedAt());
        assertTrue(processedOrders.containsKey("ORD-1"));
    }

    @Test
    void handleOrderMarksInvalidOrderAsFailed() {
        Order order = new Order("ORD-bad", "", OrderTypeValue.URGENT);

        consumer.handleOrder(order, "test-thread");

        assertEquals(OrderStatus.FAILED, order.getStatus());
        assertEquals(order, processedOrders.get("ORD-bad"));
    }

    @Test
    void runProcessesOrdersFromQueue() throws InterruptedException {
        queue.put(new Order("ORD-2", "Алексей", OrderTypeValue.URGENT));
        queue.put(new Order("ORD-3", "Мария", OrderTypeValue.REGULAR));
        running.set(false);

        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(5000);

        assertEquals(2, processedOrders.size());
        assertEquals(OrderStatus.PROCESSED, processedOrders.get("ORD-2").getStatus());
        assertEquals(OrderStatus.PROCESSED, processedOrders.get("ORD-3").getStatus());
    }

    @Test
    void urgentOrderProcessedFasterThanRegular() throws InterruptedException {
        Order urgent = new Order("ORD-U", "Клиент", OrderTypeValue.URGENT);
        Order regular = new Order("ORD-R", "Клиент", OrderTypeValue.REGULAR);

        long urgentStart = System.currentTimeMillis();
        consumer.handleOrder(urgent, "t1");
        long urgentTime = System.currentTimeMillis() - urgentStart;

        long regularStart = System.currentTimeMillis();
        consumer.handleOrder(regular, "t2");
        long regularTime = System.currentTimeMillis() - regularStart;

        assertTrue(urgentTime < regularTime);
    }
}
