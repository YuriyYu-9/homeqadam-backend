package com.homeqadam.backend.profile;

import com.homeqadam.backend.category.ServiceCategory;
import com.homeqadam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1 профиль = 1 пользователь
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "first_name", length = 60)
    private String firstName;

    @Column(name = "last_name", length = 60)
    private String lastName;

    @Column(length = 30)
    private String phone;

    @Column(length = 80)
    private String telegram;

    // Для TECHNICIAN можно хранить URL на фото (позже сделаем upload)
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    // ===== TECHNICIAN анкета =====

    @Enumerated(EnumType.STRING)
    @Column(name = "specialty", length = 50)
    private ServiceCategory specialty; // специальность мастера

    @Column(name = "experience_years")
    private Integer experienceYears; // опыт (лет)

    @Column(name = "about", columnDefinition = "text")
    private String about; // описание опыта/о себе

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
