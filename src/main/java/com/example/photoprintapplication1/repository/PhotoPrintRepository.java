/*package com.example.photoprintapplication.repository;

import com.example.photoprintapplication.models.*;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PhotoPrintRepository {
    // Хранилища данных
    private final Map<Long, Customer> customers = new HashMap<>();
    private final Map<Long, Format> formats = new HashMap<>();
    private final Map<Long, Photo> photos = new HashMap<>();
    private final Map<Long, Order> orders = new HashMap<>();
    private final Map<Long, Delivery> deliveries = new HashMap<>();

    // Счетчики ID
    private final AtomicLong customerId = new AtomicLong(1);
    private final AtomicLong formatId = new AtomicLong(1);
    private final AtomicLong photoId = new AtomicLong(1);
    private final AtomicLong orderId = new AtomicLong(1);
    private final AtomicLong deliveryId = new AtomicLong(1);

    // Customer методы
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(customerId.getAndIncrement());
        }
        customers.put(customer.getId(), customer);
        return customer;
    }

    public List<Customer> findAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public Optional<Customer> findCustomerById(Long id) {
        return Optional.ofNullable(customers.get(id));
    }

    public void deleteCustomerById(Long id) {
        customers.remove(id);
    }

    // Format методы
    public Format save(Format format) {
        if (format.getId() == null) {
            format.setId(formatId.getAndIncrement());
        }
        formats.put(format.getId(), format);
        return format;
    }

    public List<Format> findAllFormats() {
        return new ArrayList<>(formats.values());
    }

    public Optional<Format> findFormatById(Long id) {
        return Optional.ofNullable(formats.get(id));
    }

    public void deleteFormatById(Long id) {
        formats.remove(id);
    }

    // Order методы
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(orderId.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }

    public List<Order> findAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public Optional<Order> findOrderById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public void deleteOrderById(Long id) {
        orders.remove(id);
    }

    // Photo методы
    public Photo save(Photo photo) {
        if (photo.getId() == null) {
            photo.setId(photoId.getAndIncrement());
        }
        photos.put(photo.getId(), photo);
        return photo;
    }

    public List<Photo> findAllPhotos() {
        return new ArrayList<>(photos.values());
    }

    public Optional<Photo> findPhotoById(Long id) {
        return Optional.ofNullable(photos.get(id));
    }

    public void deletePhotoById(Long id) {
        photos.remove(id);
    }

    // Delivery методы
    public Delivery save(Delivery delivery) {
        if (delivery.getId() == null) {
            delivery.setId(deliveryId.getAndIncrement());
        }
        deliveries.put(delivery.getId(), delivery);
        return delivery;
    }

    public List<Delivery> findAllDeliveries() {
        return new ArrayList<>(deliveries.values());
    }

    public Optional<Delivery> findDeliveryById(Long id) {
        return Optional.ofNullable(deliveries.get(id));
    }

    public void deleteDeliveryById(Long id) {
        deliveries.remove(id);
    }

    // Специальные методы для бизнес-логики
    public boolean existsDeliveryForOrder(Long orderId) {
        return deliveries.values().stream()
                .anyMatch(delivery -> delivery.getOrderId().equals(orderId));
    }

    public Optional<Delivery> findDeliveryByOrderId(Long orderId) {
        return deliveries.values().stream()
                .filter(delivery -> delivery.getOrderId().equals(orderId))
                .findFirst();
    }
}*/