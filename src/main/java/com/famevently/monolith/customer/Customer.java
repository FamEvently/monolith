package com.famevently.monolith.customer;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Customer {
    private Long userId;
    private String email;
    private boolean isGuest;
    private String firstName;
    private String lastName;
    private String pwdHash;
    private String language;
    private String gender;
    private LocalDate birthday;
    private boolean isWhitelisted;
    private OffsetDateTime createdAt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean GetIsGuest() {
        return isGuest;
    }

    public void setIsGuest(boolean guest) {
        isGuest = guest;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPwdHash() {
        return pwdHash;
    }

    public void setPwdHash(String pwdHash) {
        this.pwdHash = pwdHash;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public boolean getIsWhitelisted() {
        return isWhitelisted;
    }

    public void setIsWhitelisted(boolean whitelisted) {
        isWhitelisted = whitelisted;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
