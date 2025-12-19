package com.homeqadam.backend.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String address;

    /**
     * Категория в виде строки, одна из:
     * ELECTRICITY, PLUMBING, CLEANING, APPLIANCE_REPAIR
     */
    @NotBlank
    private String category;

    // Необязательное поле: когда примерно нужно выполнить заказ
    private LocalDateTime scheduledAt;
}


