package com.famevently.monolith.register;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class RegisterRequest {

    @NotBlank
    @Email()
    private String email;

    //private boolean isGuest;

    @NotBlank
    @Size(min=1,max=100)
    private String firstName;

    @NotBlank
    @Size(min=1,max=100)
    private String lastName;
    @NotBlank
    @Size(min=8)
    private String password;

    @NotBlank
    private String language;

    @NotBlank
    private String gender;

    @NotNull
    @Past
    private LocalDate birthday;
    //private boolean isWhitelisted;
    //private OffsetDateTime createdAt;


    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getLanguage() {
        return language;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }
}
