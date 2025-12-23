package com.homeqadam.backend.review.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;

    private Long orderId;

    private Long clientId;
    private String clientFirstName;
    private String clientLastName;

    private Long technicianId;
    private String technicianFirstName;
    private String technicianLastName;

    private Integer rating;
    private String text;

    private LocalDateTime createdAt;
}
