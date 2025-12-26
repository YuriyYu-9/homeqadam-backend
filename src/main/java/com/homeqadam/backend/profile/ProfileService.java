package com.homeqadam.backend.profile;

import com.homeqadam.backend.category.ServiceCategory;
import com.homeqadam.backend.profile.dto.ProfileResponse;
import com.homeqadam.backend.profile.dto.ProfileUpsertRequest;
import com.homeqadam.backend.profile.dto.PublicTechnicianListItemResponse;
import com.homeqadam.backend.profile.dto.PublicTechnicianProfileResponse;
import com.homeqadam.backend.user.Role;
import com.homeqadam.backend.user.User;
import com.homeqadam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(Long userId) {
        Profile profile = profileRepository.findByUserId(userId).orElse(null);

        if (profile == null) {
            return ProfileResponse.builder()
                    .id(null)
                    .userId(userId)
                    .completed(false)
                    .build();
        }
        return toResponse(profile);
    }

    @Transactional
    public ProfileResponse upsertMyProfile(Long userId, ProfileUpsertRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> Profile.builder().user(user).build());

        profile.setFirstName(normalizeRequired(req.getFirstName(), "firstName"));
        profile.setLastName(normalizeRequired(req.getLastName(), "lastName"));
        profile.setPhone(normalizeRequired(req.getPhone(), "phone"));

        profile.setTelegram(normalizeOptional(req.getTelegram()));

        if (user.getRole() == Role.TECHNICIAN) {
            profile.setAvatarUrl(normalizeOptional(req.getAvatarUrl()));
        } else {
            profile.setAvatarUrl(null);
        }

        if (user.getRole() == Role.TECHNICIAN) {
            ServiceCategory specialty = parseSpecialty(req.getSpecialty());
            Integer exp = req.getExperienceYears();
            String about = normalizeOptional(req.getAbout());

            if (specialty == null) {
                throw new IllegalArgumentException("Specialty is required for technician");
            }
            if (exp == null || exp < 0 || exp > 80) {
                throw new IllegalArgumentException("Experience years must be between 0 and 80");
            }
            if (about == null || about.isBlank()) {
                throw new IllegalArgumentException("About is required for technician");
            }

            profile.setSpecialty(specialty);
            profile.setExperienceYears(exp);
            profile.setAbout(about.trim());
        } else {
            profile.setSpecialty(null);
            profile.setExperienceYears(null);
            profile.setAbout(null);
        }

        profile = profileRepository.save(profile);
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public PublicTechnicianProfileResponse getPublicTechnicianProfile(Long userId) {
        Profile profile = profileRepository
                .findByUserIdAndUserRole(userId, Role.TECHNICIAN)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Technician not found"));

        return PublicTechnicianProfileResponse.builder()
                .userId(profile.getUser().getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .specialty(profile.getSpecialty())
                .experienceYears(profile.getExperienceYears())
                .about(profile.getAbout())
                .avatarUrl(profile.getAvatarUrl())
                .telegram(profile.getTelegram())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PublicTechnicianListItemResponse> getPublicTechnicians() {
        return profileRepository.findByUserRole(Role.TECHNICIAN)
                .stream()
                // показываем только заполненные анкеты (чтобы не было пустых карточек)
                .filter(this::isCompleted)
                .map(p -> PublicTechnicianListItemResponse.builder()
                        .userId(p.getUser().getId())
                        .firstName(p.getFirstName())
                        .lastName(p.getLastName())
                        .specialty(p.getSpecialty())
                        .experienceYears(p.getExperienceYears())
                        .avatarUrl(p.getAvatarUrl())
                        .telegram(p.getTelegram())
                        .build())
                .toList();
    }

    public boolean isCompleted(Profile profile) {
        if (profile == null) return false;

        Role role = profile.getUser().getRole();

        boolean baseCompleted =
                notBlank(profile.getFirstName()) &&
                        notBlank(profile.getLastName()) &&
                        notBlank(profile.getPhone());

        if (!baseCompleted) return false;

        if (role == Role.CLIENT) return true;

        if (role == Role.TECHNICIAN) {
            return profile.getSpecialty() != null
                    && profile.getExperienceYears() != null
                    && profile.getExperienceYears() >= 0
                    && notBlank(profile.getAbout());
        }

        return true;
    }

    private ProfileResponse toResponse(Profile p) {
        return ProfileResponse.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .phone(p.getPhone())
                .telegram(p.getTelegram())
                .avatarUrl(p.getAvatarUrl())
                .specialty(p.getSpecialty())
                .experienceYears(p.getExperienceYears())
                .about(p.getAbout())
                .completed(isCompleted(p))
                .build();
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String normalizeOptional(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private String normalizeRequired(String s, String fieldName) {
        if (s == null) throw new IllegalArgumentException(fieldName + " is required");
        String t = s.trim();
        if (t.isEmpty()) throw new IllegalArgumentException(fieldName + " is required");
        return t;
    }

    private ServiceCategory parseSpecialty(String raw) {
        if (raw == null) return null;
        String t = raw.trim();
        if (t.isEmpty()) return null;
        try {
            return ServiceCategory.valueOf(t.toUpperCase());
        } catch (Exception ex) {
            String allowed = Arrays.stream(ServiceCategory.values())
                    .map(Enum::name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            throw new IllegalArgumentException("Invalid specialty. Allowed: " + allowed);
        }
    }
}
