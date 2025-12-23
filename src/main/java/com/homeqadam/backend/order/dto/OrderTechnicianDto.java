package com.homeqadam.backend.order.dto;

import com.homeqadam.backend.category.ServiceCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTechnicianDto {

    private Long userId;

    private String firstName;
    private String lastName;

    private ServiceCategory specialty;
    private Integer experienceYears;

    private String avatarUrl;
    private String telegram;
}
