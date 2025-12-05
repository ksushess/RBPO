package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Order;
import com.example.photoprintapplication.models.Photo;
import com.example.photoprintapplication.models.Format;
import com.example.photoprintapplication.models.User;
import com.example.photoprintapplication.repository.OrderRepository;
import com.example.photoprintapplication.repository.PhotoRepository;
import com.example.photoprintapplication.repository.UserRepository;
import com.example.photoprintapplication.repository.FormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/business")
public class OrderServiceController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FormatRepository formatRepository;


    @PostMapping("/orders/calculate")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Object createOrderWithCalculation(@RequestBody Map<String, Object> request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Проверяем наличие photos в запросе
            if (!request.containsKey("photos")) {
                return Map.of("error", "Photos are required");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> photosData = (List<Map<String, Object>>) request.get("photos");

            if (photosData == null || photosData.isEmpty()) {
                return Map.of("error", "At least one photo is required");
            }

            Order order = new Order();
            order.setUser(currentUser);
            order.setCustomer(currentUser.getCustomer());
            order.setStatus(Order.OrderStatus.CREATED);

            List<Photo> photos = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            // Рассчитываем стоимость на основе форматов
            for (Map<String, Object> photoData : photosData) {
                // Проверяем наличие format в photo
                if (!photoData.containsKey("format")) {
                    return Map.of("error", "Format is required for each photo");
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> formatData = (Map<String, Object>) photoData.get("format");

                if (!formatData.containsKey("id")) {
                    return Map.of("error", "Format ID is required");
                }

                Long formatId;
                try {
                    formatId = Long.valueOf(formatData.get("id").toString());
                } catch (NumberFormatException e) {
                    return Map.of("error", "Invalid format ID");
                }

                // Получаем формат из базы
                Format format = formatRepository.findById(formatId)
                        .orElseThrow(() -> new RuntimeException("Format not found with id: " + formatId));

                // Создаем фото
                Photo photo = new Photo();
                photo.setFilename((String) photoData.get("filename"));
                photo.setDescription((String) photoData.get("description"));
                photo.setFormat(format);
                photo.setOrder(order);

                photos.add(photo);
                total = total.add(format.getPrice());
            }

            order.setPhotos(photos);
            order.setTotalPrice(total);

            Order savedOrder = orderRepository.save(order);

            // Возвращаем упрощенный ответ с информацией о заказе
            return Map.of(
                    "orderId", savedOrder.getId(),
                    "status", savedOrder.getStatus(),
                    "totalPrice", savedOrder.getTotalPrice(),
                    "photosCount", savedOrder.getPhotos().size(),
                    "message", "Order created successfully with automatic price calculation"
            );

        } catch (RuntimeException e) {
            return Map.of("error", "Failed to create order: " + e.getMessage());
        } catch (Exception e) {
            return Map.of("error", "Internal server error: " + e.getMessage());
        }
    }


    @GetMapping("/statistics/popular-formats")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getPopularFormats() {
        try {
            List<Object[]> formatStats = photoRepository.findPopularFormats();

            // Если нет статистики, возвращаем пустой список
            if (formatStats == null || formatStats.isEmpty()) {
                return Map.of("popularFormats", Collections.emptyList());
            }

            List<Map<String, Object>> stats = new ArrayList<>();
            for (Object[] stat : formatStats) {
                // Проверяем, что массив содержит достаточно элементов
                if (stat.length >= 3) {
                    Map<String, Object> formatStat = new HashMap<>();
                    formatStat.put("formatId", stat[0]);
                    formatStat.put("formatName", stat[1] != null ? stat[1] : "Unknown");
                    formatStat.put("photoCount", stat[2]);
                    stats.add(formatStat);
                }
            }

            return Map.of(
                    "popularFormats", stats,
                    "totalFormats", stats.size()
            );

        } catch (Exception e) {
            return Map.of(
                    "error", "Could not get format statistics",
                    "message", e.getMessage()
            );
        }
    }


    @PostMapping("/orders/calculate-price")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Map<String, Object> calculatePrice(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> photosData = (List<Map<String, Object>>) request.get("photos");

            if (photosData == null || photosData.isEmpty()) {
                return Map.of("error", "At least one photo is required");
            }

            BigDecimal total = BigDecimal.ZERO;
            List<Map<String, Object>> calculatedPhotos = new ArrayList<>();

            for (Map<String, Object> photoData : photosData) {
                @SuppressWarnings("unchecked")
                Map<String, Object> formatData = (Map<String, Object>) photoData.get("format");

                if (formatData == null || !formatData.containsKey("id")) {
                    return Map.of("error", "Format ID is required for each photo");
                }

                Long formatId;
                try {
                    formatId = Long.valueOf(formatData.get("id").toString());
                } catch (NumberFormatException e) {
                    return Map.of("error", "Invalid format ID");
                }

                Format format = formatRepository.findById(formatId)
                        .orElseThrow(() -> new RuntimeException("Format not found with id: " + formatId));

                BigDecimal photoPrice = format.getPrice();
                total = total.add(photoPrice);

                Map<String, Object> calculatedPhoto = new HashMap<>();
                calculatedPhoto.put("filename", photoData.get("filename"));
                calculatedPhoto.put("formatName", format.getName());
                calculatedPhoto.put("price", photoPrice);
                calculatedPhotos.add(calculatedPhoto);
            }

            return Map.of(
                    "totalPrice", total,
                    "photos", calculatedPhotos,
                    "photosCount", calculatedPhotos.size(),
                    "message", "Price calculated successfully"
            );

        } catch (RuntimeException e) {
            return Map.of("error", "Failed to calculate price: " + e.getMessage());
        } catch (Exception e) {
            return Map.of("error", "Internal server error: " + e.getMessage());
        }
    }


    @GetMapping("/customers/{customerId}/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }


    @GetMapping("/statistics/orders-count")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getOrdersCount() {
        try {
            Long totalOrders = orderRepository.getTotalOrdersCount();
            Long createdOrders = orderRepository.countByStatus(Order.OrderStatus.CREATED);
            Long paidOrders = orderRepository.countByStatus(Order.OrderStatus.PAID);
            Long completedOrders = orderRepository.countByStatus(Order.OrderStatus.COMPLETED);

            return Map.of(
                    "totalOrders", totalOrders != null ? totalOrders : 0,
                    "createdOrders", createdOrders != null ? createdOrders : 0,
                    "paidOrders", paidOrders != null ? paidOrders : 0,
                    "completedOrders", completedOrders != null ? completedOrders : 0
            );
        } catch (Exception e) {
            return Map.of("error", "Could not get orders statistics: " + e.getMessage());
        }
    }


    @GetMapping("/statistics/total-revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getTotalRevenue() {
        try {
            BigDecimal totalRevenue = orderRepository.getTotalRevenue();
            BigDecimal averageOrderValue = orderRepository.getAverageOrderValue();

            return Map.of(
                    "totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
                    "averageOrderValue", averageOrderValue != null ? averageOrderValue : BigDecimal.ZERO,
                    "currency", "RUB"
            );
        } catch (Exception e) {
            return Map.of("error", "Could not get revenue statistics: " + e.getMessage());
        }
    }


    @PatchMapping("/orders/{orderId}/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public Object updateOrderStatus(
            @PathVariable Long orderId,
            @PathVariable String status
    ) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Проверяем валидность статуса
            try {
                Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                order.setStatus(newStatus);

                Order updatedOrder = orderRepository.save(order);

                return Map.of(
                        "orderId", updatedOrder.getId(),
                        "newStatus", updatedOrder.getStatus(),
                        "message", "Order status updated successfully"
                );

            } catch (IllegalArgumentException e) {
                return Map.of("error", "Invalid status: " + status);
            }

        } catch (RuntimeException e) {
            return Map.of("error", "Failed to update order status: " + e.getMessage());
        }
    }


    @PatchMapping("/orders/{orderId}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public Object markAsPaid(@PathVariable Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            order.setStatus(Order.OrderStatus.PAID);
            Order updatedOrder = orderRepository.save(order);

            return Map.of(
                    "orderId", updatedOrder.getId(),
                    "status", updatedOrder.getStatus(),
                    "message", "Order marked as paid successfully"
            );

        } catch (RuntimeException e) {
            return Map.of("error", "Failed to mark order as paid: " + e.getMessage());
        }
    }


    @GetMapping("/orders/{orderId}/details")
    @PreAuthorize("hasRole('ADMIN')")
    public Object getOrderDetails(@PathVariable Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("orderId", order.getId());
            orderDetails.put("status", order.getStatus());
            orderDetails.put("totalPrice", order.getTotalPrice());
            orderDetails.put("createdAt", order.getCreatedAt());

            if (order.getUser() != null) {
                orderDetails.put("customer", Map.of(
                        "userId", order.getUser().getId(),
                        "username", order.getUser().getUsername(),
                        "email", order.getUser().getEmail()
                ));
            }

            if (order.getPhotos() != null && !order.getPhotos().isEmpty()) {
                List<Map<String, Object>> photosInfo = new ArrayList<>();
                for (Photo photo : order.getPhotos()) {
                    Map<String, Object> photoInfo = new HashMap<>();
                    photoInfo.put("photoId", photo.getId());
                    photoInfo.put("filename", photo.getFilename());
                    photoInfo.put("description", photo.getDescription());

                    if (photo.getFormat() != null) {
                        photoInfo.put("format", Map.of(
                                "formatId", photo.getFormat().getId(),
                                "formatName", photo.getFormat().getName(),
                                "price", photo.getFormat().getPrice()
                        ));
                    }
                    photosInfo.add(photoInfo);
                }
                orderDetails.put("photos", photosInfo);
            }

            return orderDetails;

        } catch (RuntimeException e) {
            return Map.of("error", "Failed to get order details: " + e.getMessage());
        }
    }
}