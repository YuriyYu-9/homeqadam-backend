package com.homeqadam.backend.profile.dto;

import com.homeqadam.backend.category.ServiceCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long id;
    private Long userId;

    private String firstName;
    private String lastName;
    private String phone;
    private String telegram;
    private String avatarUrl;

    // TECHNICIAN анкета
    private ServiceCategory specialty;
    private Integer experienceYears;
    private String about;

    private boolean completed;
}
