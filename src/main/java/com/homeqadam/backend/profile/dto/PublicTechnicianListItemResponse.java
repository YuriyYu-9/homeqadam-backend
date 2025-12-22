package com.homeqadam.backend.profile.dto;

import com.homeqadam.backend.category.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PublicTechnicianListItemResponse {

    private Long userId;

    private String firstName;
    private String lastName;

    private ServiceCategory specialty;
    private Integer experienceYears;

    private String avatarUrl;

    // по твоему решению — публичный
    private String telegram;
}
