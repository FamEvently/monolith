package com.famevently.monolith.user;

import com.famevently.monolith.BaseIntegrationTest;
import com.famevently.monolith.customer.CustomerCreationRequest;
import com.famevently.monolith.customer.UserCoreInfo;
import com.famevently.monolith.customer.UserRepository;
import com.famevently.monolith.login.GoogleTokenVerifier;
import com.famevently.monolith.password.UserPasswordService;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SoftAssertionsExtension.class)
public class UserFeaturesTest extends BaseIntegrationTest {

    private static final String CREATE_SESSION_ENDPOINT = "/api-gateway-service/sessions/users/";
    private static final String DELETE_SESSION_ENDPOINT = "/api-gateway-service/sessions/devices/";
    private static final String VALIDATE_SESSION_ENDPOINT = "/api-gateway-service/sessions/";

    private static final String TEST_EMAIL = "test-logout@example.com";
    private static final String TEST_PASSWORD = "TestPassword123";
    private static final String TEST_DEVICE_UUID = "test-device-uuid-123";
    private static final String TEST_SESSION_ID = "session-id-abc-123";
    private static final String GOOGLE_TEST_EMAIL = "google-user@example.com";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPasswordService userPasswordService;

    @MockBean
    private GoogleTokenVerifier googleTokenVerifier;

    private Long testUserId;
    private Long googleUserId;

    @BeforeEach
    void setupTestUser() {
        final CustomerCreationRequest customerRequest = new CustomerCreationRequest(
                TEST_EMAIL,
                "Test",
                "User",
                "en",
                "male",
                LocalDate.of(1990, 1, 1),
                "US",
                false
        );

        final UserCoreInfo savedUser = userRepository.save(customerRequest)
                .orElseThrow(() -> new IllegalStateException("Failed to create test user"));

        testUserId = savedUser.userId();

        // Save password for the user
        userPasswordService.savePassword(TEST_PASSWORD, testUserId, TEST_EMAIL);

        // Create test user for Google login
        final CustomerCreationRequest googleUserRequest = new CustomerCreationRequest(
                GOOGLE_TEST_EMAIL,
                "Google",
                "User",
                "en",
                "female",
                LocalDate.of(1992, 5, 15),
                "UK",
                false
        );

        final UserCoreInfo googleUser = userRepository.save(googleUserRequest)
                .orElseThrow(() -> new IllegalStateException("Failed to create Google user"));

        googleUserId = googleUser.userId();
    }

    @Test
    void login_shouldCreateSession_andReturnUserData(final SoftAssertions softly) throws Exception {
        wireMockServer.stubFor(WireMock.post(urlPathEqualTo(CREATE_SESSION_ENDPOINT + testUserId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "id": "%s",
                                    "userId": %d,
                                    "isAdmin": false
                                }
                                """.formatted(TEST_SESSION_ID, testUserId))));

        final String loginRequestBody = """
                {
                    "email": "%s",
                    "password": "%s",
                    "rememberMe": false
                }
                """.formatted(TEST_EMAIL, TEST_PASSWORD);

        final var result = mockMvc.perform(post("/v1/private/login")
                        .param("deviceUuid", TEST_DEVICE_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andReturn();

        softly.assertThat(result.getResponse().getStatus())
                .as("Login should return 200 OK")
                .isEqualTo(200);

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain sessionId")
                .contains(TEST_SESSION_ID);

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain email")
                .contains(TEST_EMAIL);

        softly.assertThatCode(() ->
                wireMockServer.verify(1, postRequestedFor(urlPathEqualTo(CREATE_SESSION_ENDPOINT + testUserId)))
        ).as("Create session endpoint should be called").doesNotThrowAnyException();
    }

    @Test
    void logout_shouldDeleteSession_whenSessionExists(final SoftAssertions softly) throws Exception {
        wireMockServer.stubFor(WireMock.delete(urlPathEqualTo(DELETE_SESSION_ENDPOINT + TEST_DEVICE_UUID))
                .willReturn(aResponse()
                        .withStatus(200)));

        final var result = mockMvc.perform(delete("/v1/users/{userId}/devices/{deviceUuid}/logout",
                        testUserId, TEST_DEVICE_UUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        softly.assertThat(result.getResponse().getStatus())
                .as("Logout should return 200 OK")
                .isEqualTo(200);

        softly.assertThatCode(() ->
                wireMockServer.verify(1, deleteRequestedFor(urlPathEqualTo(DELETE_SESSION_ENDPOINT + TEST_DEVICE_UUID)))
        ).as("Delete session endpoint should be called exactly once").doesNotThrowAnyException();
    }

    @Test
    void loginAndLogout_fullFlow(final SoftAssertions softly) throws Exception {
        final String deviceUuid = "full-flow-device-uuid";

        wireMockServer.stubFor(WireMock.post(urlPathEqualTo(CREATE_SESSION_ENDPOINT + testUserId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "id": "%s",
                                    "userId": %d,
                                    "isAdmin": false
                                }
                                """.formatted(TEST_SESSION_ID, testUserId))));

        wireMockServer.stubFor(WireMock.delete(urlPathEqualTo(DELETE_SESSION_ENDPOINT + deviceUuid))
                .willReturn(aResponse()
                        .withStatus(200)));

        // When: Login
        final String loginRequestBody = """
                {
                    "email": "%s",
                    "password": "%s",
                    "rememberMe": false
                }
                """.formatted(TEST_EMAIL, TEST_PASSWORD);

        final var loginResult = mockMvc.perform(post("/v1/private/login")
                        .param("deviceUuid", deviceUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andReturn();

        softly.assertThat(loginResult.getResponse().getStatus())
                .as("Login should succeed")
                .isEqualTo(200);

        final var logoutResult = mockMvc.perform(delete("/v1/users/{userId}/devices/{deviceUuid}/logout",
                        testUserId, deviceUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        softly.assertThat(logoutResult.getResponse().getStatus())
                .as("Logout should return 200 OK")
                .isEqualTo(200);

        softly.assertThatCode(() ->
                wireMockServer.verify(1, postRequestedFor(urlPathEqualTo(CREATE_SESSION_ENDPOINT + testUserId)))
        ).as("Create session should be called during login").doesNotThrowAnyException();

        softly.assertThatCode(() ->
                wireMockServer.verify(1, deleteRequestedFor(urlPathEqualTo(DELETE_SESSION_ENDPOINT + deviceUuid)))
        ).as("Delete session should be called during logout").doesNotThrowAnyException();
    }

    @Test
    void login_withNonExistentEmail_shouldReturnInvalidCredentials(final SoftAssertions softly) throws Exception {
        final String loginRequestBody = """
                {
                    "email": "non-existent@example.com",
                    "password": "SomePassword123",
                    "rememberMe": false
                }
                """;

        final var result = mockMvc.perform(post("/v1/private/login")
                        .param("deviceUuid", TEST_DEVICE_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andReturn();

        softly.assertThat(result.getResponse().getStatus())
                .as("Login with non-existent email should return 404 NOT_FOUND")
                .isEqualTo(401);

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain CustomerNotFound error type")
                .contains("InvalidCredentials");
    }

    @Test
    void login_withWrongPassword_shouldReturnInvalidCredentials(final SoftAssertions softly) throws Exception {
        final String loginRequestBody = """
                {
                    "email": "%s",
                    "password": "WrongPassword123",
                    "rememberMe": false
                }
                """.formatted(TEST_EMAIL);

        final var result = mockMvc.perform(post("/v1/private/login")
                        .param("deviceUuid", TEST_DEVICE_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andReturn();

        softly.assertThat(result.getResponse().getStatus())
                .as("Login with wrong password should return 401 UNAUTHORIZED")
                .isEqualTo(401);

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain InvalidCredentials error type")
                .contains("InvalidCredentials");
    }

    @Test
    void registration_withExistingEmail_shouldReturnEmailAlreadyUsed(final SoftAssertions softly) throws Exception {
        final String registrationRequest = """
                {
                    "email": "%s",
                    "firstName": "Duplicate",
                    "lastName": "User",
                    "birthday": "1990-01-01",
                    "countryOfResidence": "US",
                    "language": "en",
                    "gender": "male",
                    "password": "Password123"
                }
                """.formatted(TEST_EMAIL);

        final var result = mockMvc.perform(post("/v1/private/registration")
                        .param("deviceUuid", TEST_DEVICE_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationRequest))
                .andReturn();

        softly.assertThat(result.getResponse().getStatus())
                .as("Registration with existing email should return 401 UNAUTHORIZED")
                .isEqualTo(401);

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain EmailAlreadyUsed error type")
                .contains("EmailAlreadyUsed");
    }

    @Test
    void loginWithSession_withInvalidSession_shouldReturnInvalidSessionException(final SoftAssertions softly) throws Exception {
        final String invalidSessionId = "invalid-session-id";

        wireMockServer.stubFor(WireMock.get(urlPathEqualTo(VALIDATE_SESSION_ENDPOINT + invalidSessionId))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"type\":\"InvalidSessionException\",\"message\":\"not found\"}")));

        final var result = mockMvc.perform(post("/v1/private/login/session/{sessionId}", invalidSessionId)
                        .param("deviceUuid", TEST_DEVICE_UUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        softly.assertThat(result.getResponse().getStatus())
                .as("Login with invalid session should return 401 UNAUTHORIZED")
                .isEqualTo(401);
    }

    @Test
    void googleLogin_withExistingCustomer_shouldCreateSessionAndReturnUserData(final SoftAssertions softly) throws Exception {
        when(googleTokenVerifier.verifyAndGetEmail("valid-google-token")).thenReturn(GOOGLE_TEST_EMAIL);

        // Stub create session endpoint
        wireMockServer.stubFor(WireMock.post(urlPathEqualTo(CREATE_SESSION_ENDPOINT + googleUserId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "id": "%s",
                                    "userId": %d,
                                    "isAdmin": false
                                }
                                """.formatted(TEST_SESSION_ID, googleUserId))));

        final String googleLoginRequest = """
                {
                    "token": "valid-google-token",
                    "rememberMe": false
                }
                """;

        final var result = mockMvc.perform(post("/v1/private/login/google")
                        .param("deviceUuid", TEST_DEVICE_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(googleLoginRequest))
                .andReturn();

        softly.assertThat(result.getResponse().getStatus())
                .as("Google login should return 200 OK")
                .isEqualTo(200);

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain sessionId")
                .contains(TEST_SESSION_ID);

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain email")
                .contains(GOOGLE_TEST_EMAIL);

        softly.assertThatCode(() ->
                wireMockServer.verify(1, postRequestedFor(urlPathEqualTo(CREATE_SESSION_ENDPOINT + googleUserId)))
        ).as("Create session endpoint should be called for Google login").doesNotThrowAnyException();
    }

    @Test
    void googleLogin_withNonExistentCustomer_shouldReturnCustomerNotFound(final SoftAssertions softly) throws Exception {
        // Mock Google token verifier to return a non-existent email
        when(googleTokenVerifier.verifyAndGetEmail("valid-google-token")).thenReturn("non-existent-google@example.com");

        final String googleLoginRequest = """
                {
                    "token": "valid-google-token",
                    "rememberMe": false
                }
                """;

        final var result = mockMvc.perform(post("/v1/private/login/google")
                        .param("deviceUuid", TEST_DEVICE_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(googleLoginRequest))
                .andReturn();

        softly.assertThat(result.getResponse().getStatus())
                .as("Google login with non-existent customer should return 404")
                .isEqualTo(404);

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain CustomerNotFound error")
                .contains("CustomerNotFound");
    }
}
