package ru.student.taskmanager;

import org.junit.jupiter.api.Test;
import ru.student.taskmanager.model.Order;
import ru.student.taskmanager.model.OrderStatus;
import ru.student.taskmanager.model.OrderTypeValue;
import ru.student.taskmanager.service.OrderProducer;
import ru.student.taskmanager.service.TaskManager;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.atomic.AtomicInteger;

class TaskManagerTest {

    @Test
    void startProcessesAllOrders() throws InterruptedException {
        TaskManager taskManager = new TaskManager(6, 2);

        taskManager.start();

        Map<String, Order> processed = taskManager.getProcessedOrders();
        assertEquals(6, processed.size());
        assertTrue(processed.values().stream()
                .allMatch(o -> o.getStatus() == OrderStatus.PROCESSED));
    }

    @Test
    void processedOrdersStoredInConcurrentHashMap() throws InterruptedException {
        TaskManager taskManager = new TaskManager(4, 2);
        taskManager.start();

        assertTrue(taskManager.getProcessedOrders() instanceof ConcurrentHashMap);
        assertEquals(4, taskManager.getProcessedOrders().size());
    }

    @Test
    void multipleConsumersProcessOrdersInParallel() throws InterruptedException {
        BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
        Map<String, Order> processed = new ConcurrentHashMap<>();
        TaskManager taskManager = new TaskManager(queue, processed, 0, 3);

        for (int i = 0; i < 8; i++) {
            queue.put(new Order("ORD-" + i, "Клиент-" + i, OrderTypeValue.REGULAR));
        }

        taskManager.start();

        assertEquals(8, processed.size());
    }

    @Test
    void producerAddsOrdersToQueue() throws InterruptedException {
        BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
        OrderProducer producer = new OrderProducer(
                queue,
                3,
                new AtomicInteger(0),
                false
        );

        Thread thread = new Thread(producer);
        thread.start();
        thread.join(3000);

        assertEquals(3, queue.size());
    }

    @Test
    void invalidSampleOrderFailsValidation() throws InterruptedException {
        TaskManager taskManager = new TaskManager(5, 2, true);
        taskManager.start();

        Order invalid = taskManager.getProcessedOrders().get("ORD-invalid");
        assertTrue(invalid != null && invalid.getStatus() == OrderStatus.FAILED);
    }

    @Test
    void consumerCountIsAtLeastTwo() {
        TaskManager taskManager = new TaskManager(3, 2);
        assertTrue(taskManager.getConsumerCount() >= 2);
    }

    @Test
    void queueAcceptsOrdersBeforeProcessing() throws InterruptedException {
        BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
        Order order = new Order("ORD-Q", "Тест", OrderTypeValue.URGENT);

        queue.offer(order, 1, TimeUnit.SECONDS);

        assertEquals(1, queue.size());
        assertEquals(order, queue.take());
    }
}
