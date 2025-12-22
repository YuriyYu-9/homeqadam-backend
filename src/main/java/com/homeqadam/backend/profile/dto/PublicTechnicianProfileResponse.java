package com.homeqadam.backend.profile.dto;

import com.homeqadam.backend.category.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PublicTechnicianProfileResponse {

    private Long userId;

    private String firstName;
    private String lastName;

    private ServiceCategory specialty;
    private Integer experienceYears;

    private String about;
    private String avatarUrl;

    // Публичное поле по твоему решению
    private String telegram;
}
