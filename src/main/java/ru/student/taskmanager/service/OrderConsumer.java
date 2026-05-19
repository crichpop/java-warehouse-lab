package ru.student.taskmanager.service;

import ru.student.taskmanager.model.Order;
import ru.student.taskmanager.model.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Забирает заказы из очереди, валидирует и обрабатывает их.
 */
public class OrderConsumer implements Runnable {

    private final BlockingQueue<Order> orderQueue;
    private final Map<String, Order> processedOrders;
    private final OrderValidator validator;
    private final OrderProcessor processor;
    private final AtomicBoolean running;

    public OrderConsumer(BlockingQueue<Order> orderQueue,
                         Map<String, Order> processedOrders,
                         OrderValidator validator,
                         OrderProcessor processor,
                         AtomicBoolean running) {
        this.orderQueue = orderQueue;
        this.processedOrders = processedOrders;
        this.validator = validator;
        this.processor = processor;
        this.running = running;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();

        while (running.get() || !orderQueue.isEmpty()) {
            try {
                Order order = orderQueue.poll(500, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (order == null) {
                    continue;
                }

                handleOrder(order, threadName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void handleOrder(Order order, String threadName) {
        System.out.println("[" + threadName + "] Начата обработка заказа " + order.getId());

        List<String> errors = validator.validate(order);
        if (!errors.isEmpty()) {
            order.setStatus(OrderStatus.FAILED);
            processedOrders.put(order.getId(), order);
            System.out.println("Ошибка валидации заказа " + order.getId() + ": " + String.join("; ", errors));
            return;
        }

        try {
            processor.process(order);
            processedOrders.put(order.getId(), order);
            System.out.println("[" + threadName + "] Заказ " + order.getId()
                    + " успешно обработан. Статус: " + order.getStatus());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            order.setStatus(OrderStatus.FAILED);
            processedOrders.put(order.getId(), order);
            System.out.println("[" + threadName + "] Обработка заказа " + order.getId() + " прервана");
        }
    }

}
