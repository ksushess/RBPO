package com.example.photoprintapplication1.controllers;

import com.example.photoprintapplication1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class OrderServiceController {

    @Autowired
    private OrderService orderService;

    // 1. Создание заказа с расчетом
    @PostMapping("/ordercreate")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Map<String, Object> createOrderWithCalculation(@RequestBody Map<String, Object> request) {
        return orderService.createOrderWithCalculation(request);
    }

    // 2. Обновление статуса заказа и доставки
    @PatchMapping("/order/{orderId}/update-status")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> updateOrderAndDeliveryStatus(
            @PathVariable Long orderId,
            @RequestParam String orderStatus,
            @RequestParam String deliveryStatus,
            @RequestParam(required = false) String trackingNumber) {
        return orderService.updateOrderAndDeliveryStatus(orderId, orderStatus, deliveryStatus, trackingNumber);
    }

    // 3. История заказов клиента
    @GetMapping("/customers/{customerId}/history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Map<String, Object>> getCustomerOrderHistory(@PathVariable Long customerId) {
        return orderService.getCustomerOrderHistory(customerId);
    }

    // 4. Статистика популярных форматов
    @GetMapping("/statistics/popular-formats")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getPopularFormats() {
        List<Map<String, Object>> stats = orderService.getPopularFormats();
        return Map.of("popularFormats", stats, "totalFormats", stats.size());
    }

    // 5. Общая статистика по заказам и доходу
    @GetMapping("/statistics/overall")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getOverallStatistics() {
        return orderService.getOverallStatistics();
    }
}