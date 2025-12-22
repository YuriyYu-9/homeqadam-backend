package com.homeqadam.backend.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileUpsertRequest {

    @NotBlank
    @Size(max = 60)
    private String firstName;

    @NotBlank
    @Size(max = 60)
    private String lastName;

    @NotBlank
    @Size(max = 30)
    private String phone;

    @Size(max = 80)
    private String telegram;

    @Size(max = 500)
    private String avatarUrl;

    // ===== TECHNICIAN анкета =====

    // ServiceCategory в виде строки: ELECTRICITY, PLUMBING, CLEANING, APPLIANCE_REPAIR
    @Size(max = 50)
    private String specialty;

    // годы опыта
    private Integer experienceYears;

    // описание опыта/о себе
    @Size(max = 3000)
    private String about;

    public ProfileUpsertRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getTelegram() { return telegram; }
    public void setTelegram(String telegram) { this.telegram = telegram; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
}
