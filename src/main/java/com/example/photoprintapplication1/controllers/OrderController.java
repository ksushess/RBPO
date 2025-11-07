package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Order;
import com.example.photoprintapplication.models.Photo;
import com.example.photoprintapplication.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    public Object create(@RequestBody Order order) {
        // Проверка на пустой заказ
        if (order.getPhotos() == null || order.getPhotos().isEmpty()) {
            return "Order must contain at least one photo";
        }

        // Устанавливаем связи для фото
        for (Photo photo : order.getPhotos()) {
            photo.setOrder(order);
        }

        // Устанавливаем связь для доставки
        if (order.getDelivery() != null) {
            order.getDelivery().setOrder(order);
        }

        // Пересчитываем стоимость
        order.calculateTotalPrice();

        return orderRepository.save(order);
    }

    @GetMapping
    public List<Order> all() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable Long id, @RequestBody Order order) {
        Order exist = orderRepository.findById(id).orElse(null);
        if (exist == null) return null;

        exist.setStatus(order.getStatus());

        // Обновляем доставку если передана
        if (order.getDelivery() != null) {
            if (exist.getDelivery() == null) {
                exist.setDelivery(order.getDelivery());
                exist.getDelivery().setOrder(exist);
            } else {
                exist.getDelivery().setAddress(order.getDelivery().getAddress());
                exist.getDelivery().setStatus(order.getDelivery().getStatus());
                exist.getDelivery().setTrackingNumber(order.getDelivery().getTrackingNumber());
            }
        }

        // Обновляем фото если переданы
        if (order.getPhotos() != null) {
            exist.getPhotos().clear();
            exist.getPhotos().addAll(order.getPhotos());
            for (Photo photo : exist.getPhotos()) {
                photo.setOrder(exist);
            }
            exist.calculateTotalPrice();
        }

        return orderRepository.save(exist);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "ok";
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return null;

        order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        return orderRepository.save(order);
    }

    @PatchMapping("/{id}/pay")
    public Order markAsPaid(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return null;

        order.setStatus(Order.OrderStatus.PAID);
        return orderRepository.save(order);
    }
}