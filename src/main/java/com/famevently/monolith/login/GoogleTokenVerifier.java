package com.famevently.monolith.login;

import com.famevently.monolith.customer.UserCoreInfo;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Objects.isNull;

@Component
public class GoogleTokenVerifier {



    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${google.oauth2.client-id}") String clientId) {
        this.verifier = buildVerifier(clientId);
    }

    private static GoogleIdTokenVerifier buildVerifier(String clientId) {
        return new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Arrays.asList(
                        clientId,
                        "407408718192.apps.googleusercontent.com" // <--- The Playground's Client ID
                ))
                .build();
    }

    public String verifyAndGetEmail(final String idTokenString) {
        try
        {
            final GoogleIdToken token = verifier.verify(idTokenString);

            if (isNull(token))
            {
                throw new InvalidGoogleCredentials();
            }

            final GoogleIdToken.Payload payload = token.getPayload();
            return payload.getEmail();
        } catch (final GeneralSecurityException | IOException e) {
            throw new InvalidGoogleCredentials();
        }
    }
}

