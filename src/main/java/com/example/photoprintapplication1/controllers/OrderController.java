package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Order;
import com.example.photoprintapplication.models.Photo;
import com.example.photoprintapplication.models.User;
import com.example.photoprintapplication.repository.OrderRepository;
import com.example.photoprintapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public Object create(@RequestBody Order order) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Устанавливаем связи
        order.setUser(currentUser);
        order.setCustomer(currentUser.getCustomer());

        // Устанавливаем связи для фото
        if (order.getPhotos() != null) {
            for (Photo photo : order.getPhotos()) {
                photo.setOrder(order);
            }
        }

        // Устанавливаем связь для доставки
        if (order.getDelivery() != null) {
            order.getDelivery().setOrder(order);
        }

        order.calculateTotalPrice();
        return orderRepository.save(order);
    }


    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public List<Order> getMyOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserId(currentUser.getId());
    }


    @GetMapping("/my-orders/{id}")
    @PreAuthorize("hasRole('USER')")
    public Order getMyOrder(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));


        if (order.getUser() == null || !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Access denied: You can only view your own orders");
        }

        return order;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Order getOrder(@PathVariable Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Order update(@PathVariable Long id, @RequestBody Order order) {
        Order exist = orderRepository.findById(id).orElse(null);
        if (exist == null) return null;
        exist.setStatus(order.getStatus());
        return orderRepository.save(exist);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "ok";
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Order updateStatus(@PathVariable Long id, @RequestBody String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return null;
        order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        return orderRepository.save(order);
    }

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public Order markAsPaid(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return null;
        order.setStatus(Order.OrderStatus.PAID);
        return orderRepository.save(order);
    }
}