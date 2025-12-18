package com.example.photoprintapplication1.service;

import com.example.photoprintapplication1.models.*;
import com.example.photoprintapplication1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private FormatRepository formatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Transactional
    public Map<String, Object> createOrderWithCalculation(Map<String, Object> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> photosData = (List<Map<String, Object>>) request.get("photos");
        if (photosData == null || photosData.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one photo is required");

        Order order = new Order();
        order.setUser(currentUser);
        // Убираем обязательность customer — пусть будет null, если нет
        // order.setCustomer(currentUser.getCustomer());
        order.setStatus(Order.OrderStatus.CREATED);

        List<Photo> photos = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map<String, Object> photoData : photosData) {
            String filename = (String) photoData.get("filename");
            String description = (String) photoData.get("description");

            @SuppressWarnings("unchecked")
            Map<String, Object> formatData = (Map<String, Object>) photoData.get("format");
            if (formatData == null || !formatData.containsKey("id")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Format ID is required");
            }
            Long formatId = Long.valueOf(formatData.get("id").toString());
            Format format = formatRepository.findById(formatId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Format not found: " + formatId));

            Photo photo = new Photo();
            photo.setFilename(filename != null ? filename : "photo.jpg");
            photo.setDescription(description);
            photo.setFormat(format);
            photo.setOrder(order);
            photos.add(photo);
            total = total.add(format.getPrice() != null ? format.getPrice() : BigDecimal.ZERO);
        }

        order.setPhotos(photos);
        order.setTotalPrice(total);
        Order savedOrder = orderRepository.save(order);

        return Map.of(
                "orderId", savedOrder.getId(),
                "status", savedOrder.getStatus().name(),
                "totalPrice", savedOrder.getTotalPrice(),
                "photosCount", savedOrder.getPhotos().size(),
                "message", "Заказ успешно создан"
        );
    }

    @Transactional
    public Map<String, Object> updateOrderAndDeliveryStatus(Long orderId, String orderStatus, String deliveryStatus, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        order.setStatus(Order.OrderStatus.valueOf(orderStatus.toUpperCase()));

        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElse(new Delivery());
        delivery.setStatus(Delivery.DeliveryStatus.valueOf(deliveryStatus.toUpperCase()));
        if (trackingNumber != null) {
            delivery.setTrackingNumber(trackingNumber);
        }
        delivery.setOrder(order);
        deliveryRepository.save(delivery);

        orderRepository.save(order);
        return Map.of(
                "orderId", order.getId(),
                "newOrderStatus", order.getStatus(),
                "newDeliveryStatus", delivery.getStatus(),
                "message", "Status updated successfully"
        );
    }

    public List<Map<String, Object>> getCustomerOrderHistory(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        List<Map<String, Object>> history = new ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderId", order.getId());
            orderMap.put("status", order.getStatus());
            orderMap.put("totalPrice", order.getTotalPrice());
            orderMap.put("photos", order.getPhotos());
            history.add(orderMap);
        }
        return history;
    }

    public List<Map<String, Object>> getPopularFormats() {
        List<Object[]> formatStats = photoRepository.findPopularFormats();
        List<Map<String, Object>> stats = new ArrayList<>();
        for (Object[] stat : formatStats) {
            if (stat.length >= 3) {
                Map<String, Object> formatStat = new HashMap<>();
                formatStat.put("formatId", stat[0]);
                formatStat.put("formatName", stat[1]);
                formatStat.put("photoCount", stat[2]);
                stats.add(formatStat);
            }
        }
        return stats;
    }

    public Map<String, Object> getOverallStatistics() {
        Long totalOrders = orderRepository.count();
        Long createdOrders = orderRepository.countByStatus(Order.OrderStatus.CREATED);
        Long paidOrders = orderRepository.countByStatus(Order.OrderStatus.PAID);
        Long completedOrders = orderRepository.countByStatus(Order.OrderStatus.COMPLETED);
        BigDecimal totalRevenue = orderRepository.getTotalRevenue() != null ? orderRepository.getTotalRevenue() : BigDecimal.ZERO;
        BigDecimal averageOrderValue = orderRepository.getAverageOrderValue() != null ? orderRepository.getAverageOrderValue() : BigDecimal.ZERO;

        return Map.of(
                "totalOrders", totalOrders,
                "createdOrders", createdOrders,
                "paidOrders", paidOrders,
                "completedOrders", completedOrders,
                "totalRevenue", totalRevenue,
                "averageOrderValue", averageOrderValue
        );
    }
}