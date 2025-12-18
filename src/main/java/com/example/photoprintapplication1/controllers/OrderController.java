package com.example.photoprintapplication1.controllers;

import com.example.photoprintapplication1.models.*;
import com.example.photoprintapplication1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FormatRepository formatRepository;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // ← Изменено: теперь USER + ADMIN
    @Transactional
    public Order create(@RequestBody Map<String, Object> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String statusStr = (String) request.get("status");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> photosData = (List<Map<String, Object>>) request.get("photos");

        Order order = new Order();
        order.setUser(currentUser);
        order.setStatus(statusStr != null ? Order.OrderStatus.valueOf(statusStr.toUpperCase()) : Order.OrderStatus.CREATED);

        // Customer optional
        Long customerId = request.containsKey("customerId") ? Long.parseLong(request.get("customerId").toString()) : null;
        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + customerId));
            order.setCustomer(customer);
        } else if (currentUser.getCustomer() != null) {
            order.setCustomer(currentUser.getCustomer());
        }

        List<Photo> photos = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        if (photosData != null && !photosData.isEmpty()) {
            for (Map<String, Object> photoData : photosData) {
                String filename = (String) photoData.get("filename");
                String description = (String) photoData.get("description");
                Long formatId = photoData.containsKey("formatId") ? Long.parseLong(photoData.get("formatId").toString()) : null;

                if (formatId == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "formatId required for each photo");
                }

                Format format = formatRepository.findById(formatId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Format not found: " + formatId));

                Photo photo = new Photo();
                photo.setFilename(filename != null ? filename : "default.jpg");
                photo.setDescription(description != null ? description : "");
                photo.setFormat(format);
                photo.setOrder(order);
                photos.add(photo);
                total = total.add(format.getPrice() != null ? format.getPrice() : BigDecimal.ZERO);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one photo is required");
        }

        order.setPhotos(photos);
        order.setTotalPrice(total);

        return orderRepository.save(order);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Order getOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Order update(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id));

        if (request.containsKey("status")) {
            order.setStatus(Order.OrderStatus.valueOf(((String) request.get("status")).toUpperCase()));
        }

        return orderRepository.save(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "ok";
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public List<Order> getMyOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return orderRepository.findByUserId(currentUser.getId());
    }
}