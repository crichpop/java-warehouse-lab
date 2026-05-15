package ru.student.taskmanager.service;

import ru.student.taskmanager.model.Order;
import ru.student.taskmanager.model.OrderStatus;
import ru.student.taskmanager.model.OrderTypeValue;

import java.time.LocalDateTime;

/**
 * Имитирует обработку заказа (задержка через sleep).
 */
public class OrderProcessor {

    private final long urgentProcessingMs;
    private final long regularProcessingMs;

    public OrderProcessor() {
        this(300, 800);
    }

    public OrderProcessor(long urgentProcessingMs, long regularProcessingMs) {
        this.urgentProcessingMs = urgentProcessingMs;
        this.regularProcessingMs = regularProcessingMs;
    }

    public void process(Order order) throws InterruptedException {
        order.setStatus(OrderStatus.PROCESSING);

        long delay = order.getType() == OrderTypeValue.URGENT
                ? urgentProcessingMs
                : regularProcessingMs;

        if (order.getType() == OrderTypeValue.URGENT) {
            System.out.println("[СРОЧНЫЙ] Заказ " + order.getId() + " обрабатывается в приоритетном режиме");
        }

        Thread.sleep(delay);

        order.setStatus(OrderStatus.PROCESSED);
        order.setProcessedAt(LocalDateTime.now());
    }
}
