package com.homeqadam.backend.profile;

import com.homeqadam.backend.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Optional<Profile> findByUserIdAndUserRole(Long userId, Role role);

    // ✅ список профилей по роли (TECHNICIAN)
    List<Profile> findByUserRole(Role role);
}
