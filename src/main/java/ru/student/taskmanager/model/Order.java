package ru.student.taskmanager.model;

import ru.student.taskmanager.annotation.OrderType;
import ru.student.taskmanager.annotation.Validate;

import java.time.LocalDateTime;
import java.util.Objects;

public class Order {

    @Validate(message = "Идентификатор заказа не может быть пустым")
    private String id;

    @Validate(message = "Имя клиента не может быть пустым")
    private String customerName;

    @Validate(message = "Тип заказа должен быть указан")
    @OrderType
    private OrderTypeValue type;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private OrderStatus status;

    public Order() {
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public Order(String id, String customerName, OrderTypeValue type) {
        this();
        this.id = id;
        this.customerName = customerName;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public OrderTypeValue getType() {
        return type;
    }

    public void setType(OrderTypeValue type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{"
                + "id='" + id + '\''
                + ", customerName='" + customerName + '\''
                + ", type=" + type
                + ", status=" + status
                + ", createdAt=" + createdAt
                + ", processedAt=" + processedAt
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
