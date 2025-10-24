package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Delivery;
import com.example.photoprintapplication.models.Order;
import com.example.photoprintapplication.repository.PhotoPrintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    @Autowired
    private PhotoPrintRepository repo;

    @PostMapping
    public Object create(@RequestBody Delivery delivery) {
        // Проверяем что заказ существует
        if (delivery.getOrderId() == null) {
            return "orderId is required";
        }

        Order order = repo.findOrderById(delivery.getOrderId()).orElse(null);
        if (order == null) {
            return "Order not found";
        }

        // Проверяем что заказ оплачен
        if (order.getStatus() != Order.OrderStatus.PAID) {
            return "Delivery can only be created for paid orders";
        }

        // Проверяем что для заказа еще нет доставки
        if (repo.existsDeliveryForOrder(delivery.getOrderId())) {
            return "Delivery already exists for this order";
        }

        return repo.save(delivery);
    }

    @GetMapping
    public List<Delivery> all() {
        return repo.findAllDeliveries();
    }

    @GetMapping("/{id}")
    public Delivery get(@PathVariable Long id) {
        return repo.findDeliveryById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Delivery update(@PathVariable Long id, @RequestBody Delivery delivery) {
        Delivery exist = repo.findDeliveryById(id).orElse(null);
        if (exist == null) return null;

        exist.setAddress(delivery.getAddress());
        exist.setTrackingNumber(delivery.getTrackingNumber());
        exist.setStatus(delivery.getStatus());

        return repo.save(exist);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteDeliveryById(id);
        return "ok";
    }
}