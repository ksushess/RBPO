package com.example.photoprintapplication1.controllers;

import com.example.photoprintapplication1.models.Delivery;
import com.example.photoprintapplication1.models.Order;
import com.example.photoprintapplication1.repository.DeliveryRepository;
import com.example.photoprintapplication1.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveries")
@PreAuthorize("hasRole('ADMIN')")
public class DeliveryController {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    public Delivery create(@RequestBody Map<String, Object> request) {
        String address = (String) request.get("address");
        String trackingNumber = (String) request.get("trackingNumber");
        String statusStr = (String) request.get("status");
        Long orderId = request.containsKey("orderId") ? Long.parseLong(request.get("orderId").toString()) : null;

        Delivery delivery = new Delivery();
        delivery.setAddress(address != null ? address : "");
        delivery.setTrackingNumber(trackingNumber != null ? trackingNumber : "");
        delivery.setStatus(statusStr != null ? Delivery.DeliveryStatus.valueOf(statusStr.toUpperCase()) : Delivery.DeliveryStatus.PENDING);

        if (orderId != null) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId));
            delivery.setOrder(order);
        }

        return deliveryRepository.save(delivery);
    }

    @GetMapping
    public List<Delivery> all() {
        return deliveryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Delivery get(@PathVariable Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found: " + id));
    }

    @PutMapping("/{id}")
    public Delivery update(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found: " + id));

        if (request.containsKey("address")) {
            delivery.setAddress((String) request.get("address"));
        }
        if (request.containsKey("trackingNumber")) {
            delivery.setTrackingNumber((String) request.get("trackingNumber"));
        }
        if (request.containsKey("status")) {
            delivery.setStatus(Delivery.DeliveryStatus.valueOf(((String) request.get("status")).toUpperCase()));
        }
        if (request.containsKey("orderId")) {
            Long orderId = Long.parseLong(request.get("orderId").toString());
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId));
            delivery.setOrder(order);
        }

        return deliveryRepository.save(delivery);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        deliveryRepository.deleteById(id);
        return "ok";
    }
}