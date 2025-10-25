package com.example.photoprintapplication.models;

public class Delivery {
    private Long id;
    private String address;
    private String trackingNumber;
    private DeliveryStatus status = DeliveryStatus.PENDING;
    private Long orderId;

    public enum DeliveryStatus {
        PENDING, SHIPPED, DELIVERED, CANCELLED
    }

    public Delivery() {}
    public Delivery(String address, Long orderId) {
        this.address = address;
        this.orderId = orderId;
    }
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}