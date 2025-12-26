package com.homeqadam.backend.order;

import com.homeqadam.backend.order.dto.OrderRequest;
import com.homeqadam.backend.order.dto.OrderResponse;
import com.homeqadam.backend.security.details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse create(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody OrderRequest request
    ) {
        return orderService.createOrder(
                user.getUser().getId(),
                request
        );
    }

    @GetMapping("/my")
    public List<OrderResponse> myOrders(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return orderService.getMyOrders(
                user.getUser().getId()
        );
    }

    @PostMapping("/{orderId}/cancel")
    public OrderResponse cancel(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return orderService.cancelByClient(
                orderId,
                user.getUser().getId()
        );
    }

    @PostMapping("/{orderId}/complete")
    public OrderResponse complete(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return orderService.completeByClient(
                orderId,
                user.getUser().getId()
        );
    }
}
