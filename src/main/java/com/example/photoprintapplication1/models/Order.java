package com.example.photoprintapplication.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();
    private OrderStatus status = OrderStatus.CREATED;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private Long customerId;
    private List<Photo> photos = new ArrayList<>();
    private Delivery delivery;

    public enum OrderStatus {
        CREATED, PAID, IN_PROGRESS, COMPLETED, CANCELLED
    }

    public void calculateTotalPrice() {
        this.totalPrice = photos.stream()
                .map(photo -> photo.getFormat().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public List<Photo> getPhotos() { return photos; }
    public void setPhotos(List<Photo> photos) { this.photos = photos; }

    public Delivery getDelivery() { return delivery; }
    public void setDelivery(Delivery delivery) { this.delivery = delivery; }
}