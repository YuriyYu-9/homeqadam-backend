package com.homeqadam.backend.order;

import com.homeqadam.backend.order.dto.OrderResponse;
import com.homeqadam.backend.security.details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/technician/orders")
public class TechnicianOrdersController {

    private final OrderService orderService;

    /**
     * ✅ Новый основной эндпоинт:
     * категория берётся из профиля мастера в БД.
     */
    @GetMapping("/available")
    public List<OrderResponse> available(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return orderService.getAvailableOrdersForTechnician(principal.getUser().getId());
    }

    /**
     * ✅ Legacy (не обязательно, но удобно оставить на время)
     * чтобы можно было тестить руками:
     * /technician/orders/available-legacy?category=ELECTRICITY
     */
    @GetMapping("/available-legacy")
    public List<OrderResponse> availableLegacy(@RequestParam("category") String category) {
        return orderService.getAvailableOrdersForTechnicianLegacy(category);
    }

    @PostMapping("/{orderId}/accept")
    public OrderResponse accept(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return orderService.acceptOrder(orderId, principal.getUser().getId());
    }

    @PostMapping("/{orderId}/start")
    public OrderResponse start(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return orderService.startOrder(orderId, principal.getUser().getId());
    }

    @GetMapping("/taken")
    public List<OrderResponse> taken(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return orderService.getTakenOrders(principal.getUser().getId());
    }
}
