package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Order;
import com.example.photoprintapplication.repository.OrderRepository;
import com.example.photoprintapplication.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class OrderServiceController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PhotoRepository photoRepository;

    // 1. Создание заказа с расчетом стоимости (упрощенный)
    @PostMapping("/orders/calculate")
    public Order createOrderWithCalculation(@RequestBody Map<String, Object> request) {
        Long customerId = Long.valueOf(request.get("customerId").toString());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> photosData = (List<Map<String, Object>>) request.get("photos");

        Order order = new Order();
        order.setCustomerId(customerId);

        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> photoData : photosData) {
            @SuppressWarnings("unchecked")
            Map<String, Object> formatData = (Map<String, Object>) photoData.get("format");
            Long formatId = Long.valueOf(formatData.get("id").toString());

            total = total.add(getPriceByFormatId(formatId));
        }

        order.setTotalPrice(total);
        return orderRepository.save(order);
    }

    // 2. Поиск заказов по клиенту (простой)
    @GetMapping("/customers/{customerId}/orders")
    public List<Order> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    // 3. Статистика по заказам (простая)
    @GetMapping("/statistics/orders-count")
    public Map<String, Long> getOrdersCount() {
        return Map.of("totalOrders", orderRepository.getTotalOrdersCount());
    }

    // 4. Статистика по популярным форматам (простая заглушка)
    @GetMapping("/statistics/popular-formats")
    public Map<String, Object> getPopularFormats() {
        try {
            // Получаем статистику из базы
            List<Object[]> formatStats = photoRepository.findPopularFormats();

            Map<String, Object> result = new HashMap<>();
            result.put("totalFormats", formatStats.size());

            List<Map<String, Object>> stats = new ArrayList<>();
            for (Object[] stat : formatStats) {
                Map<String, Object> formatInfo = new HashMap<>();
                formatInfo.put("formatId", stat[0]);
                formatInfo.put("formatName", stat[1]);
                formatInfo.put("photoCount", stat[2]);
                stats.add(formatInfo);
            }

            result.put("popularFormats", stats);
            return result;
        } catch (Exception e) {
            // Если есть ошибка, возвращаем сообщение об ошибке
            return Map.of(
                    "error", "Could not get format statistics",
                    "message", e.getMessage()
            );
        }
    }

    // 5. Обновление статуса заказа (простой)
    @PatchMapping("/orders/{orderId}/status/{status}")
    public Order updateOrderStatus(@PathVariable Long orderId, @PathVariable String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        return orderRepository.save(order);
    }

    // Вспомогательный метод для цен
    private BigDecimal getPriceByFormatId(Long formatId) {
        return switch (formatId.intValue()) {
            case 1 -> new BigDecimal("50.00");
            case 2 -> new BigDecimal("80.00");
            case 3 -> new BigDecimal("150.00");
            case 4 -> new BigDecimal("300.00");
            case 5 -> new BigDecimal("450.00");
            default -> new BigDecimal("100.00");
        };
    }
}