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

    public User(UUID id, String firstName, String lastName, String email, String password,
                Role role, JapaneseLevel japaneseLevel, String objective) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.japaneseLevel = japaneseLevel;
        this.objective = objective;
        this.active = true;
        this.createdAt = LocalDateTime.now();
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
