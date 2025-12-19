package com.homeqadam.backend.order.dto;

import com.homeqadam.backend.category.ServiceCategory;
import com.homeqadam.backend.order.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private ServiceCategory category;
    private String title;
    private String description;
    private String address;
    private LocalDateTime scheduledAt;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
