package com.homeqadam.backend.order;

import com.homeqadam.backend.category.ServiceCategory;
import com.homeqadam.backend.order.dto.OrderRequest;
import com.homeqadam.backend.order.dto.OrderResponse;
import com.homeqadam.backend.user.User;
import com.homeqadam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // Создание заказа от имени указанного пользователя
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ServiceCategory category = parseCategory(request.getCategory());

        Order order = Order.builder()
                .customer(customer)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .scheduledAt(request.getScheduledAt())
                .status(OrderStatus.NEW)
                .build();

        order = orderRepository.save(order);
        return toResponse(order);
    }

    // Список заказов текущего пользователя
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .category(order.getCategory())
                .title(order.getTitle())
                .description(order.getDescription())
                .address(order.getAddress())
                .scheduledAt(order.getScheduledAt())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private ServiceCategory parseCategory(String raw) {
        try {
            return ServiceCategory.valueOf(raw.trim().toUpperCase());
        } catch (Exception ex) {
            String allowed = Arrays.stream(ServiceCategory.values())
                    .map(Enum::name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            throw new IllegalArgumentException("Invalid category. Allowed: " + allowed);
        }
    }
}


