package com.homeqadam.backend.order;

import com.homeqadam.backend.order.dto.OrderRequest;
import com.homeqadam.backend.order.dto.OrderResponse;
import com.homeqadam.backend.security.details.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Создать заказ (нужен авторизованный пользователь)
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OrderRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = userDetails.getUser().getId();

        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.ok(response);
    }

    // Получить свои заказы
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = userDetails.getUser().getId();

        List<OrderResponse> orders = orderService.getMyOrders(userId);
        return ResponseEntity.ok(orders);
    }
}
