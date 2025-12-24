package com.famevently.monolith.password;

import io.micrometer.common.util.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordValidationService {

    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    public static boolean validatePassword(final String requestedPassword, final String hashedPassword) {
        if (StringUtils.isEmpty(hashedPassword) || StringUtils.isEmpty(requestedPassword)) {
            return false;
        }

        try {
            return BCrypt.checkpw(requestedPassword, hashedPassword);
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }
}
