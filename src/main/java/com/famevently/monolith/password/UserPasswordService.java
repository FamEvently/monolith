package com.famevently.monolith.password;

import org.springframework.stereotype.Service;

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
        final UserPassword userPassword = passwordRepository.getByEmail(email).orElseThrow(() -> new InvalidCredentials(email));

        if (!PasswordValidationService.validatePassword(requestedPassword, userPassword.passwordHash())) {
            throw new InvalidCredentials(email);
        }

        return AuthenticatedPassword.of(userPassword.userId());
    }

}
