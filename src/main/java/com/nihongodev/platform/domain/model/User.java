package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private JapaneseLevel japaneseLevel;
    private String objective;
    private String avatarUrl;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    public static User create(String firstName, String lastName, String email, String encodedPassword, Role role, JapaneseLevel level, String objective) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.firstName = firstName;
        user.lastName = lastName;
        user.email = email;
        user.password = encodedPassword;
        user.role = role;
        user.japaneseLevel = level != null ? level : JapaneseLevel.BEGINNER;
        user.objective = objective;
        user.active = true;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
        return user;
    }

    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String firstName, String lastName, JapaneseLevel level, String objective) {
        if (firstName != null) this.firstName = firstName;
        if (lastName != null) this.lastName = lastName;
        if (level != null) this.japaneseLevel = level;
        if (objective != null) this.objective = objective;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public JapaneseLevel getJapaneseLevel() { return japaneseLevel; }
    public void setJapaneseLevel(JapaneseLevel japaneseLevel) { this.japaneseLevel = japaneseLevel; }
    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
