package com.homeqadam.backend.profile;

import com.homeqadam.backend.profile.dto.ProfileResponse;
import com.homeqadam.backend.profile.dto.ProfileUpsertRequest;
import com.homeqadam.backend.security.details.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(profileService.getMyProfile(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> upsert(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileUpsertRequest request
    ) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(profileService.upsertMyProfile(userId, request));
    }
}
