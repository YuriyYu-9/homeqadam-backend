package com.homeqadam.backend.auth;

import com.homeqadam.backend.profile.Profile;
import com.homeqadam.backend.profile.ProfileRepository;
import com.homeqadam.backend.profile.ProfileService;
import com.homeqadam.backend.security.details.CustomUserDetails;
import com.homeqadam.backend.user.Role;
import com.homeqadam.backend.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    public AuthController(AuthService authService, ProfileRepository profileRepository, ProfileService profileService) {
        this.authService = authService;
        this.profileRepository = profileRepository;
        this.profileService = profileService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@Valid @RequestBody VerifyRequest request) {
        authService.verify(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = principal.getUser();

        Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        boolean profileCompleted = profileService.isCompleted(profile);

        String firstName = profile != null ? profile.getFirstName() : null;
        String lastName = profile != null ? profile.getLastName() : null;

        return ResponseEntity.ok(
                new MeResponse(user.getId(), user.getEmail(), user.getRole(), profileCompleted, firstName, lastName)
        );
    }

    public record MeResponse(
            Long id,
            String email,
            Role role,
            boolean profileCompleted,
            String firstName,
            String lastName
    ) {}
}
