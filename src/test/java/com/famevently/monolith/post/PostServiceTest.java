package com.famevently.monolith.post;

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
public class PostServiceTest extends BaseIntegrationTest {

    private static final String TEST_EMAIL = "post-test@example.com";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long organizerUserId;
    private Long regularUserId;

    @BeforeEach
    void setupTestUsers() {
        // Create organizer user
        final CustomerCreationRequest organizerRequest = new CustomerCreationRequest(
                TEST_EMAIL,
                "Post",
                "Organizer",
                "en",
                "male",
                LocalDate.of(1988, 3, 20),
                "US",
                false
        );
        final UserCoreInfo organizer = userRepository.save(organizerRequest)
                .orElseThrow(() -> new IllegalStateException("Failed to create organizer"));
        organizerUserId = organizer.userId();

        // Create regular user
        final CustomerCreationRequest regularRequest = new CustomerCreationRequest(
                "regular-poster@example.com",
                "Regular",
                "Poster",
                "en",
                "female",
                LocalDate.of(1992, 7, 10),
                "CA",
                false
        );
        final UserCoreInfo regular = userRepository.save(regularRequest)
                .orElseThrow(() -> new IllegalStateException("Failed to create regular user"));
        regularUserId = regular.userId();
    }

    private Long createTestEvent(final Long userId) throws Exception {
        final String eventRequest = """
                {
                    "overview": "Test Event for Posts",
                    "category": "STARTUP",
                    "eventDate": "2026-06-15",
                    "address": "123 Event Street",
                    "location": "New York",
                    "additionalInfo": null,
                    "minAge": null,
                    "maxAge": null,
                    "adultsOnly": false
                }
                """;

        final var result = mockMvc.perform(post("/v1/events/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventRequest))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("eventId").asLong();
    }

    @Test
    void createEventPost_asOrganizer_shouldMarkIsOrganizerTrue(final SoftAssertions softly) throws Exception {
        // Create event as organizer
        final Long eventId = createTestEvent(organizerUserId);

        // Create event post as organizer
        final String postRequest = """
                {
                    "eventId": %d,
                    "description": "Organizer's post about the event!",
                    "imageCount": 2,
                    "parentPostId": null
                }
                """.formatted(eventId);

        final var result = mockMvc.perform(post("/v1/posts/users/{userId}/event", organizerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequest))
                .andExpect(status().isOk())
                .andReturn();

        final var responseJson = objectMapper.readTree(result.getResponse().getContentAsString());

        softly.assertThat(responseJson.has("postId"))
                .as("Response should contain postId")
                .isTrue();

        softly.assertThat(responseJson.get("isOrganizer").asBoolean())
                .as("Post should be marked as organizer post")
                .isTrue();

        softly.assertThat(responseJson.get("postType").asText())
                .as("Post type should be EVENT")
                .isEqualTo("EVENT");
    }

    @Test
    void createEventPost_asNonOrganizer_shouldMarkIsOrganizerFalse(final SoftAssertions softly) throws Exception {
        // Create event as organizer
        final Long eventId = createTestEvent(organizerUserId);

        // Create event post as regular user (not the organizer)
        final String postRequest = """
                {
                    "eventId": %d,
                    "description": "Regular user's post about the event",
                    "imageCount": 1,
                    "parentPostId": null
                }
                """.formatted(eventId);

        final var result = mockMvc.perform(post("/v1/posts/users/{userId}/event", regularUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequest))
                .andExpect(status().isOk())
                .andReturn();

        final var responseJson = objectMapper.readTree(result.getResponse().getContentAsString());

        softly.assertThat(responseJson.get("isOrganizer").asBoolean())
                .as("Post should NOT be marked as organizer post")
                .isFalse();

        softly.assertThat(responseJson.get("postType").asText())
                .as("Post type should be EVENT")
                .isEqualTo("EVENT");
    }

    @Test
    void createBlogPost_shouldCreateBlogTypePost(final SoftAssertions softly) throws Exception {
        final String blogRequest = """
                {
                    "description": "This is my blog post content. Sharing my thoughts!",
                    "imageCount": 3
                }
                """;

        final var result = mockMvc.perform(post("/v1/posts/users/{userId}/blog", regularUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blogRequest))
                .andExpect(status().isOk())
                .andReturn();

        final var responseJson = objectMapper.readTree(result.getResponse().getContentAsString());

        softly.assertThat(responseJson.has("postId"))
                .as("Response should contain postId")
                .isTrue();

        softly.assertThat(responseJson.get("postType").asText())
                .as("Post type should be BLOG")
                .isEqualTo("BLOG");

        softly.assertThat(responseJson.get("isOrganizer").asBoolean())
                .as("Blog post should have isOrganizer false")
                .isFalse();
    }

    @Test
    void createRepost_shouldReferenceParentPost(final SoftAssertions softly) throws Exception {
        // Create original event and post
        final Long eventId = createTestEvent(organizerUserId);

        final String originalPost = """
                {
                    "eventId": %d,
                    "description": "Original post to be reposted",
                    "imageCount": 1,
                    "parentPostId": null
                }
                """.formatted(eventId);

        final var originalResult = mockMvc.perform(post("/v1/posts/users/{userId}/event", organizerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(originalPost))
                .andExpect(status().isOk())
                .andReturn();

        final String parentPostId = objectMapper.readTree(originalResult.getResponse().getContentAsString())
                .get("postId").asText();

        // Create repost
        final String repostRequest = """
                {
                    "eventId": %d,
                    "description": "Check out this amazing event!",
                    "parentPostId": "%s"
                }
                """.formatted(eventId, parentPostId);

        final var repostResult = mockMvc.perform(post("/v1/posts/users/{userId}/repost", regularUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(repostRequest))
                .andExpect(status().isOk())
                .andReturn();

        final var responseJson = objectMapper.readTree(repostResult.getResponse().getContentAsString());

        softly.assertThat(responseJson.has("postId"))
                .as("Response should contain postId")
                .isTrue();

        softly.assertThat(responseJson.get("parentPostId").asText())
                .as("Repost should reference parent post")
                .isEqualTo(parentPostId);
    }

    @Test
    void getPostsByUser_shouldReturnAllUserPosts(final SoftAssertions softly) throws Exception {
        // Create multiple posts for user
        final String blog1 = """
                {
                    "description": "First blog post",
                    "imageCount": 0
                }
                """;
        final String blog2 = """
                {
                    "description": "Second blog post",
                    "imageCount": 1
                }
                """;

        mockMvc.perform(post("/v1/posts/users/{userId}/blog", regularUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blog1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/posts/users/{userId}/blog", regularUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blog2))
                .andExpect(status().isOk());

        // Get user's posts
        final var result = mockMvc.perform(get("/v1/posts/users/{userId}", regularUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final var postsArray = objectMapper.readTree(result.getResponse().getContentAsString());

        softly.assertThat(postsArray.isArray())
                .as("Response should be an array")
                .isTrue();

        softly.assertThat(postsArray.size())
                .as("User should have 2 posts")
                .isEqualTo(2);
    }

    @Test
    void getEventPosts_shouldReturnPostsForEvent(final SoftAssertions softly) throws Exception {
        final Long eventId = createTestEvent(organizerUserId);

        // Create posts for the event
        final String post1 = """
                {
                    "eventId": %d,
                    "description": "Event post 1",
                    "imageCount": 0,
                    "parentPostId": null
                }
                """.formatted(eventId);

        final String post2 = """
                {
                    "eventId": %d,
                    "description": "Event post 2",
                    "imageCount": 1,
                    "parentPostId": null
                }
                """.formatted(eventId);

        mockMvc.perform(post("/v1/posts/users/{userId}/event", organizerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(post1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/posts/users/{userId}/event", regularUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(post2))
                .andExpect(status().isOk());

        // Get event posts
        final var result = mockMvc.perform(get("/v1/posts/events/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final var postsArray = objectMapper.readTree(result.getResponse().getContentAsString());

        softly.assertThat(postsArray.size())
                .as("Event should have 2 posts")
                .isEqualTo(2);
    }

    @Test
    void getBlogPosts_shouldReturnOnlyBlogPosts(final SoftAssertions softly) throws Exception {
        // Create blog post
        final String blogRequest = """
                {
                    "description": "A blog post for listing",
                    "imageCount": 0
                }
                """;

        mockMvc.perform(post("/v1/posts/users/{userId}/blog", regularUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blogRequest))
                .andExpect(status().isOk());

        // Get blog posts
        final var result = mockMvc.perform(get("/v1/posts/blog")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final var postsArray = objectMapper.readTree(result.getResponse().getContentAsString());

        softly.assertThat(postsArray.isArray())
                .as("Response should be an array")
                .isTrue();

        // Verify all returned posts are BLOG type
        for (int i = 0; i < postsArray.size(); i++) {
            softly.assertThat(postsArray.get(i).get("postType").asText())
                    .as("All posts should be BLOG type")
                    .isEqualTo("BLOG");
        }
    }

    @Test
    void deletePost_shouldRemovePost(final SoftAssertions softly) throws Exception {
        // Create a blog post
        final String blogRequest = """
                {
                    "description": "Post to be deleted",
                    "imageCount": 0
                }
                """;

        final var createResult = mockMvc.perform(post("/v1/posts/users/{userId}/blog", regularUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blogRequest))
                .andExpect(status().isOk())
                .andReturn();

        final String postId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("postId").asText();

        // Delete the post
        mockMvc.perform(delete("/v1/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Try to get the deleted post - should return 400 BAD_REQUEST (PostNotFoundException)
        final var getResult = mockMvc.perform(get("/v1/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        softly.assertThat(getResult.getResponse().getStatus())
                .as("Getting deleted post should return 400 BAD_REQUEST")
                .isEqualTo(400);

        softly.assertThat(getResult.getResponse().getContentAsString())
                .as("Response should contain PostNotFound error type")
                .contains("PostNotFound");
    }

    @Test
    void getPostById_nonExistentPost_shouldReturnBadRequest(final SoftAssertions softly) throws Exception {
        final String nonExistentPostId = "non-existent-post-id-12345";

        final var result = mockMvc.perform(get("/v1/posts/{postId}", nonExistentPostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        softly.assertThat(result.getResponse().getStatus())
                .as("Getting non-existent post should return 400 BAD_REQUEST")
                .isEqualTo(400);

        softly.assertThat(result.getResponse().getContentAsString())
                .as("Response should contain PostNotFound error type")
                .contains("PostNotFound");
    }
}

