package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Delivery;
import com.example.photoprintapplication.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    @Autowired
    private DeliveryRepository deliveryRepository;

    @PostMapping
    public Delivery create(@RequestBody Delivery delivery) {
        // Если передан заказ с ID, устанавливаем связь
        if (delivery.getOrder() != null && delivery.getOrder().getId() != null) {
            // Связь уже установлена через объект Order
        }
        return deliveryRepository.save(delivery);
    }

    @GetMapping
    public List<Delivery> all() {
        return deliveryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Delivery get(@PathVariable Long id) {
        return deliveryRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Delivery update(@PathVariable Long id, @RequestBody Delivery delivery) {
        Delivery exist = deliveryRepository.findById(id).orElse(null);
        if (exist == null) return null;

        exist.setAddress(delivery.getAddress());
        exist.setTrackingNumber(delivery.getTrackingNumber());
        exist.setStatus(delivery.getStatus());

        // Обновляем заказ если передан
        if (delivery.getOrder() != null && delivery.getOrder().getId() != null) {
            exist.setOrder(delivery.getOrder());
        }

        return deliveryRepository.save(exist);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        deliveryRepository.deleteById(id);
        return "ok";
    }
}