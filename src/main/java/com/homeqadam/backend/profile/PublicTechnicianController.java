package com.homeqadam.backend.profile;

import com.homeqadam.backend.profile.dto.PublicTechnicianListItemResponse;
import com.homeqadam.backend.profile.dto.PublicTechnicianProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/technicians")
public class PublicTechnicianController {

    private final ProfileService profileService;

    @GetMapping
    public List<PublicTechnicianListItemResponse> list() {
        return profileService.getPublicTechnicians();
    }

    @GetMapping("/{userId}")
    public PublicTechnicianProfileResponse getOne(@PathVariable Long userId) {
        return profileService.getPublicTechnicianProfile(userId);
    }
}
