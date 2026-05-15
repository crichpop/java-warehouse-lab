package ru.student.taskmanager.service;

import ru.student.taskmanager.model.Order;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Запускает Producer, Consumer-потоки и корректно завершает ExecutorService.
 */
public class TaskManager {

    private static final int CONSUMER_COUNT = 2;
    private static final int DEFAULT_ORDERS_COUNT = 10;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 30;

    private final BlockingQueue<Order> orderQueue;
    private final Map<String, Order> processedOrders;
    private final OrderValidator validator;
    private final OrderProcessor processor;
    private final int ordersToCreate;
    private final int consumerCount;
    private final boolean addInvalidSample;

    private ExecutorService executorService;
    private final AtomicBoolean consumersRunning = new AtomicBoolean(true);

    public TaskManager() {
        this(DEFAULT_ORDERS_COUNT, CONSUMER_COUNT, true);
    }

    public TaskManager(int ordersToCreate, int consumerCount) {
        this(ordersToCreate, consumerCount, false);
    }

    public TaskManager(int ordersToCreate, int consumerCount, boolean addInvalidSample) {
        this.orderQueue = new LinkedBlockingQueue<>();
        this.processedOrders = new ConcurrentHashMap<>();
        this.validator = new OrderValidator();
        this.processor = new OrderProcessor();
        this.ordersToCreate = ordersToCreate;
        this.consumerCount = consumerCount;
        this.addInvalidSample = addInvalidSample;
    }

    public TaskManager(BlockingQueue<Order> orderQueue,
                       Map<String, Order> processedOrders,
                       int ordersToCreate,
                       int consumerCount) {
        this.orderQueue = orderQueue;
        this.processedOrders = processedOrders;
        this.validator = new OrderValidator();
        this.processor = new OrderProcessor();
        this.ordersToCreate = ordersToCreate;
        this.consumerCount = consumerCount;
        this.addInvalidSample = false;
    }

    public void start() throws InterruptedException {
        executorService = Executors.newFixedThreadPool(consumerCount + 1);
        consumersRunning.set(true);

        OrderProducer producer = new OrderProducer(orderQueue, ordersToCreate,
                new java.util.concurrent.atomic.AtomicInteger(0), addInvalidSample);

        for (int i = 0; i < consumerCount; i++) {
            OrderConsumer consumer = new OrderConsumer(
                    orderQueue, processedOrders, validator, processor, consumersRunning);
            executorService.submit(consumer);
        }

        executorService.submit(() -> {
            try {
                producer.run();
            } finally {
                consumersRunning.set(false);
            }
        });
        executorService.shutdown();

        if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            consumersRunning.set(false);
            executorService.shutdownNow();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }

        consumersRunning.set(false);
        System.out.println("Итого обработано заказов: " + processedOrders.size());
    }

    public BlockingQueue<Order> getOrderQueue() {
        return orderQueue;
    }

    public Map<String, Order> getProcessedOrders() {
        return processedOrders;
    }

    public int getConsumerCount() {
        return consumerCount;
    }
}
