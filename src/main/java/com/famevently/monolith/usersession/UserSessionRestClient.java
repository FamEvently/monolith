package com.famevently.monolith.usersession;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
public class UserSessionRestClient {

    public static final String X_FAM_CLIENT = "X-Fam-Client";
    private final String createSessionEndpoint;
    private final String deleteSessionByDeviceUuidEndpoint;
    private final String validateSessionEndpoint;
    private final HttpServletRequest httpRequest;
    private final RestTemplate restTemplate;

    public UserSessionRestClient(@Value("${services.user-sessions.create-session}") final String createSessionEndpoint,
                                 @Value("${services.user-sessions.delete-session-by-device-uuid}") final String deleteSessionByDeviceUuidEndpoint,
                                 @Value("${services.user-sessions.validate-session}") final String validateSessionEndpoint,
                                 @Value("${services.user-sessions.connection-timeout-in-millis}") final int connectionTimeoutInMillis,
                                 @Value("${services.user-sessions.read-timeout-in-millis}") final int readTimeoutInMillis,
                                 final HttpServletRequest httpRequest)
    {
        this.createSessionEndpoint = createSessionEndpoint;
        this.deleteSessionByDeviceUuidEndpoint = deleteSessionByDeviceUuidEndpoint;
        this.validateSessionEndpoint = validateSessionEndpoint;
        this.httpRequest = httpRequest;
        this.restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(connectionTimeoutInMillis))
                .setReadTimeout(Duration.ofMillis(readTimeoutInMillis))
                .build();
    }

    public UserSession createSession(final long userId, final boolean isAdmin) {
        final String uri = UriComponentsBuilder.fromHttpUrl(createSessionEndpoint)
                .buildAndExpand(userId)
                .toUriString();
        final CreateSessionRequest createSessionRequest = new CreateSessionRequest(isAdmin);

        final MultiValueMap<String, String> headers = attachRequestHeaders();

        final HttpEntity<CreateSessionRequest> requestEntity = new HttpEntity<>(createSessionRequest, headers);
        return restTemplate.postForObject(uri, requestEntity, UserSession.class);
    }

    public Optional<UserSession> validateSession(final String sessionId, final String deviceUuid) {
        final String uri = UriComponentsBuilder.fromHttpUrl(validateSessionEndpoint)
                .queryParam("deviceUuid", deviceUuid)
                .buildAndExpand(sessionId)
                .toUriString();

        try {
            return Optional.ofNullable(restTemplate.exchange(uri, HttpMethod.GET, null, UserSession.class).getBody());
        } catch (final Exception e)
        {
            return Optional.empty();
        }
    }

    public void deleteSession(final String deviceUuid)
    {
        final String uri = UriComponentsBuilder.fromHttpUrl(deleteSessionByDeviceUuidEndpoint)
                .buildAndExpand(deviceUuid)
                .toUriString();

        restTemplate.exchange(uri, HttpMethod.DELETE, null, Object.class);
    }

    private MultiValueMap<String, String> attachRequestHeaders() {
        final LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        try
        {
            final String header = httpRequest.getHeader(X_FAM_CLIENT);
            if (!isNull(header)) {
                headers.add(X_FAM_CLIENT, header);
            }
        } catch (final IllegalStateException e) {
            //log error
        }
        return headers;
    }
}
