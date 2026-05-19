package ru.student.taskmanager.service;

import ru.student.taskmanager.model.Order;
import ru.student.taskmanager.model.OrderTypeValue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Создаёт заказы и кладёт их в общую очередь.
 */
public class OrderProducer implements Runnable {

    private final BlockingQueue<Order> orderQueue;
    private final int ordersToCreate;
    private final AtomicInteger orderCounter;
    private final boolean addInvalidSample;

    public OrderProducer(BlockingQueue<Order> orderQueue, int ordersToCreate,
                         AtomicInteger orderCounter, boolean addInvalidSample) {
        this.orderQueue = orderQueue;
        this.ordersToCreate = ordersToCreate;
        this.orderCounter = orderCounter;
        this.addInvalidSample = addInvalidSample;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < ordersToCreate; i++) {
                Order order = createOrderForIndex(i);
                System.out.println("Создан заказ: " + order);

                orderQueue.put(order);
                System.out.println("Заказ " + order.getId() + " добавлен в очередь (размер очереди: "
                        + orderQueue.size() + ")");

                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Order createOrderForIndex(int index) {
        if (addInvalidSample && index == ordersToCreate - 1) {
            return new Order("ORD-invalid", "", OrderTypeValue.REGULAR);
        }
        int number = orderCounter.incrementAndGet();
        String id = "ORD-" + number;
        String customerName = "Клиент-" + number;
        OrderTypeValue type = (index % 3 == 0) ? OrderTypeValue.URGENT : OrderTypeValue.REGULAR;
        return new Order(id, customerName, type);
    }

    public Order createOrder(String id, String customerName, OrderTypeValue type) {
        return new Order(id, customerName, type);
    }
}
