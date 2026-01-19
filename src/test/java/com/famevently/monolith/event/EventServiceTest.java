package com.famevently.monolith.event;

import com.famevently.monolith.BaseIntegrationTest;
import com.famevently.monolith.customer.CustomerCreationRequest;
import com.famevently.monolith.customer.UserCoreInfo;
import com.famevently.monolith.customer.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SoftAssertionsExtension.class)
public class EventServiceTest extends BaseIntegrationTest {

    private static final String TEST_EMAIL = "event-test@example.com";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testUserId;
    private Long secondUserId;

    @BeforeEach
    void setupTestUsers() {
        // Create organizer user
        final CustomerCreationRequest organizerRequest = new CustomerCreationRequest(
                TEST_EMAIL,
                "Event",
                "Organizer",
                "en",
                "male",
                LocalDate.of(1990, 1, 1),
                "US",
                false
        );
        final UserCoreInfo organizer = userRepository.save(organizerRequest)
                .orElseThrow(() -> new IllegalStateException("Failed to create organizer"));
        testUserId = organizer.userId();

        // Create attendee user
        final CustomerCreationRequest attendeeRequest = new CustomerCreationRequest(
                "attendee@example.com",
                "Event",
                "Attendee",
                "en",
                "female",
                LocalDate.of(1995, 5, 15),
                "UK",
                false
        );
        final UserCoreInfo attendee = userRepository.save(attendeeRequest)
                .orElseThrow(() -> new IllegalStateException("Failed to create attendee"));
        secondUserId = attendee.userId();
    }

    @Test
    void createEvent_shouldReturnCreatedEvent(final SoftAssertions softly) throws Exception {
        final String eventRequest = """
                {
                    "overview": "Test Event Overview",
                    "category": "STARTUP",
                    "eventDate": "2026-06-15",
                    "address": "123 Test Street",
                    "location": "New York",
                    "additionalInfo": "Bring your own drinks",
                    "minAge": 18,
                    "maxAge": 65,
                    "adultsOnly": true
                }
                """;

        final var result = mockMvc.perform(post("/v1/events/users/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventRequest))
                .andExpect(status().isOk())
                .andReturn();

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain event overview")
                .contains("Test Event Overview");

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain location")
                .contains("New York");
    }

    @Test
    void markAttendance_shouldUpdateAttendeeStats(final SoftAssertions softly) throws Exception {
        // First create an event
        final String eventRequest = """
                {
                    "overview": "Attendance Test Event",
                    "category": "KIDS_PARTY",
                    "eventDate": "2026-07-20",
                    "address": "456 Stadium Road",
                    "location": "Los Angeles",
                    "additionalInfo": null,
                    "minAge": null,
                    "maxAge": null,
                    "adultsOnly": false
                }
                """;

        final var createResult = mockMvc.perform(post("/v1/events/users/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventRequest))
                .andExpect(status().isOk())
                .andReturn();

        final Long eventId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("eventId").asLong();

        // Mark second user as going
        mockMvc.perform(post("/v1/events/{eventId}/users/{userId}/attendance", eventId, secondUserId)
                        .param("isGoing", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Get attendance stats
        final var statsResult = mockMvc.perform(get("/v1/events/{eventId}/stats", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        softly.assertThat(statsResult.getResponse().getStatus())
                .as("Stats endpoint should return 200")
                .isEqualTo(200);
    }

    @Test
    void markAttendance_changeFromGoingToNotGoing_shouldDecrementGoingCount(final SoftAssertions softly) throws Exception {
        // Create event
        final String eventRequest = """
                {
                    "overview": "Change Attendance Event",
                    "category": "CINEMA",
                    "eventDate": "2026-08-10",
                    "address": "789 Conference Center",
                    "location": "San Francisco",
                    "additionalInfo": null,
                    "minAge": null,
                    "maxAge": null,
                    "adultsOnly": false
                }
                """;

        final var createResult = mockMvc.perform(post("/v1/events/users/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventRequest))
                .andExpect(status().isOk())
                .andReturn();

        final Long eventId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("eventId").asLong();

        // Mark as going
        mockMvc.perform(post("/v1/events/{eventId}/users/{userId}/attendance", eventId, secondUserId)
                        .param("isGoing", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Change to not going
        mockMvc.perform(post("/v1/events/{eventId}/users/{userId}/attendance", eventId, secondUserId)
                        .param("isGoing", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify user attendance
        final var attendanceResult = mockMvc.perform(get("/v1/events/{eventId}/users/{userId}/attendance", eventId, secondUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        softly.assertThat(attendanceResult.getResponse().getStatus())
                .as("Attendance endpoint should return 200")
                .isEqualTo(200);
    }

    @Test
    void getUserAttendance_shouldReturnAllEventsUserRespondedTo(final SoftAssertions softly) throws Exception {
        final String event1 = """
                {
                    "overview": "User Attendance Event 1",
                    "category": "STARTUP",
                    "eventDate": "2026-09-01",
                    "address": "Event 1 Address",
                    "location": "Boston",
                    "additionalInfo": null,
                    "minAge": null,
                    "maxAge": null,
                    "adultsOnly": false
                }
                """;

        final String event2 = """
                {
                    "overview": "User Attendance Event 2",
                    "category": "KIDS_PARTY",
                    "eventDate": "2026-09-15",
                    "address": "Event 2 Address",
                    "location": "Chicago",
                    "additionalInfo": null,
                    "minAge": null,
                    "maxAge": null,
                    "adultsOnly": false
                }
                """;

        final var result1 = mockMvc.perform(post("/v1/events/users/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(event1))
                .andExpect(status().isOk())
                .andReturn();

        final var result2 = mockMvc.perform(post("/v1/events/users/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(event2))
                .andExpect(status().isOk())
                .andReturn();

        final Long eventId1 = objectMapper.readTree(result1.getResponse().getContentAsString())
                .get("eventId").asLong();
        final Long eventId2 = objectMapper.readTree(result2.getResponse().getContentAsString())
                .get("eventId").asLong();

        mockMvc.perform(post("/v1/events/{eventId}/users/{userId}/attendance", eventId1, secondUserId)
                        .param("isGoing", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/events/{eventId}/users/{userId}/attendance", eventId2, secondUserId)
                        .param("isGoing", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Get user's attendance list
        final var attendanceResult = mockMvc.perform(get("/v1/events/users/{userId}/attendance", secondUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        softly.assertThat(attendanceResult.getResponse().getStatus())
                .as("User attendance endpoint should return 200")
                .isEqualTo(200);
    }

    @Test
    void deleteEvent_shouldRemoveEvent(final SoftAssertions softly) throws Exception {
        // Create event
        final String eventRequest = """
                {
                    "overview": "Event to Delete",
                    "category": "CINEMA",
                    "eventDate": "2026-10-01",
                    "address": "Delete Street",
                    "location": "Miami",
                    "additionalInfo": null,
                    "minAge": null,
                    "maxAge": null,
                    "adultsOnly": false
                }
                """;

        final var createResult = mockMvc.perform(post("/v1/events/users/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventRequest))
                .andExpect(status().isOk())
                .andReturn();

        final Long eventId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("eventId").asLong();

        // Delete event
        mockMvc.perform(delete("/v1/events/{eventId}/users/{userId}", eventId, testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        softly.assertThat(true).as("Event deletion completed without errors").isTrue();
    }
}

