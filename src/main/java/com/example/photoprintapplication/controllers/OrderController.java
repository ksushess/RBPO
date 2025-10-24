package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Order;
import com.example.photoprintapplication.models.Customer;
import com.example.photoprintapplication.models.Photo;
import com.example.photoprintapplication.repository.PhotoPrintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private PhotoPrintRepository repo;

    @PostMapping
    public Object create(@RequestBody Order order) {
        // Проверка на пустой заказ
        if (order.getPhotos() == null || order.getPhotos().isEmpty()) {
            return "Order must contain at least one photo";
        }

        // Устанавливаем связи для фото
        for (Photo photo : order.getPhotos()) {
            photo.setOrderId(order.getId());
        }

        // Пересчитываем стоимость
        order.calculateTotalPrice();

        return repo.save(order);
    }

    @GetMapping
    public List<Order> all() {
        return repo.findAllOrders();
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable Long id) {
        return repo.findOrderById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable Long id, @RequestBody Order order) {
        Order exist = repo.findOrderById(id).orElse(null);
        if (exist == null) return null;

        exist.setStatus(order.getStatus());

        // Обновляем фото если переданы
        if (order.getPhotos() != null) {
            exist.getPhotos().clear();
            exist.getPhotos().addAll(order.getPhotos());
            for (Photo photo : exist.getPhotos()) {
                photo.setOrderId(exist.getId());
            }
            exist.calculateTotalPrice();
        }

        return repo.save(exist);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteOrderById(id);
        return "ok";
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody Order.OrderStatus status) {
        Order order = repo.findOrderById(id).orElse(null);
        if (order == null) return null;

        order.setStatus(status);
        return repo.save(order);
    }

    @PatchMapping("/{id}/pay")
    public Order markAsPaid(@PathVariable Long id) {
        Order order = repo.findOrderById(id).orElse(null);
        if (order == null) return null;

        order.setStatus(Order.OrderStatus.PAID);
        return repo.save(order);
    }
}