package com.homeqadam.backend.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCustomerDto {

    private Long userId;

    private String firstName;
    private String lastName;

    private String phone;
    private String telegram;
}
