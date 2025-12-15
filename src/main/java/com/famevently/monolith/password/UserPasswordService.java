package com.famevently.monolith.password;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPasswordService {

    private final PasswordRepository passwordRepository;

    public UserPasswordService(final PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    public void savePassword(final String password, final long userId, final String email) {
        final UserPassword userPassword = new UserPassword(userId, email, PasswordValidationService.hashPassword(password));
        passwordRepository.save(userPassword);
    }

    public AuthenticatedPassword validatePassword(final String email, final String requestedPassword) {
        final Optional<UserPassword> userPassword = passwordRepository.getByEmail(email);

        if (userPassword.isEmpty()) {
            throw new IllegalArgumentException("No password found for email: " + email);
        }

        if (!PasswordValidationService.validatePassword(requestedPassword, userPassword.get().passwordHash())) {
            //throw new Invalid password exception
        }

        return AuthenticatedPassword.of(userPassword.get().userId());

    }

}
