package com.famevently.monolith.post;

import com.famevently.monolith.BaseIntegrationTest;
import com.famevently.monolith.customer.CustomerCreationRequest;
import com.famevently.monolith.customer.UserCoreInfo;
import com.famevently.monolith.customer.UserRepository;
import com.famevently.monolith.postlikes.ReactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SoftAssertionsExtension.class)
public class PostLikesServiceTest extends BaseIntegrationTest {

    private static final String TEST_EMAIL = "likes-test@example.com";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long postCreatorUserId;
    private Long likerUserId;
    private Long secondLikerUserId;

    @BeforeEach
    void setupTestUsers() {
        // Create post creator
        final CustomerCreationRequest creatorRequest = new CustomerCreationRequest(
                TEST_EMAIL,
                "Post",
                "Creator",
                "en",
                "male",
                LocalDate.of(1985, 1, 1),
                "US",
                false
        );
        final UserCoreInfo creator = userRepository.save(creatorRequest)
                .orElseThrow(() -> new IllegalStateException("Failed to create post creator"));
        postCreatorUserId = creator.userId();

        // Create first liker
        final CustomerCreationRequest likerRequest = new CustomerCreationRequest(
                "liker1@example.com",
                "First",
                "Liker",
                "en",
                "female",
                LocalDate.of(1990, 5, 15),
                "UK",
                false
        );
        final UserCoreInfo liker = userRepository.save(likerRequest)
                .orElseThrow(() -> new IllegalStateException("Failed to create liker"));
        likerUserId = liker.userId();

        // Create second liker
        final CustomerCreationRequest secondLikerRequest = new CustomerCreationRequest(
                "liker2@example.com",
                "Second",
                "Liker",
                "en",
                "other",
                LocalDate.of(1995, 8, 20),
                "DE",
                false
        );
        final UserCoreInfo secondLiker = userRepository.save(secondLikerRequest)
                .orElseThrow(() -> new IllegalStateException("Failed to create second liker"));
        secondLikerUserId = secondLiker.userId();
    }

    private String createTestBlogPost() throws Exception {
        final String blogRequest = """
                {
                    "description": "Post for likes testing",
                    "imageCount": 1
                }
                """;

        final var result = mockMvc.perform(post("/v1/posts/users/{userId}/blog", postCreatorUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blogRequest))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("postId").asText();
    }

    @ParameterizedTest
    @EnumSource(ReactionType.class)
    void likePost_withDifferentReactions_shouldCreateLike(final ReactionType reactionType, final SoftAssertions softly) throws Exception {
        final String postId = createTestBlogPost();

        final String likeRequest = """
                {
                    "reactionType": "%s"
                }
                """.formatted(reactionType.name());

        // Like the post
        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(likeRequest))
                .andExpect(status().isOk());

        // Get user's like for the post
        final var likeResult = mockMvc.perform(get("/v1/posts/{postId}/likes/users/{userId}", postId, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final var likeJson = objectMapper.readTree(likeResult.getResponse().getContentAsString());

        softly.assertThat(likeJson.get("reactionName").asText())
                .as("Like should have correct reaction type")
                .isEqualTo(reactionType.name());

        softly.assertThat(likeJson.get("postId").asText())
                .as("Like should reference correct post")
                .isEqualTo(postId);

        softly.assertThat(likeJson.get("userId").asLong())
                .as("Like should be from correct user")
                .isEqualTo(likerUserId);
    }

    @ParameterizedTest
    @EnumSource(ReactionType.class)
    void likePost_shouldUpdateStats_forEachReactionType(final ReactionType reactionType, final SoftAssertions softly) throws Exception {
        final String postId = createTestBlogPost();

        final String likeRequest = """
                {
                    "reactionType": "%s"
                }
                """.formatted(reactionType.name());

        // Like the post
        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(likeRequest))
                .andExpect(status().isOk());

        // Get stats
        final var statsResult = mockMvc.perform(get("/v1/posts/{postId}/likes/stats", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final var statsJson = objectMapper.readTree(statsResult.getResponse().getContentAsString());

        softly.assertThat(statsJson.get("totalLikesCount").asInt())
                .as("Total likes count should be 1")
                .isEqualTo(1);

        // Verify the specific reaction count based on type
        final String countField = reactionType.name().toLowerCase() + "Count";
        softly.assertThat(statsJson.get(countField).asInt())
                .as("Specific reaction count should be 1 for " + reactionType)
                .isEqualTo(1);
    }

    @Test
    void likePost_multipleLikers_shouldAggregateStats(final SoftAssertions softly) throws Exception {
        final String postId = createTestBlogPost();

        // First user likes with HEART
        final String heartLike = """
                {
                    "reactionType": "HEART"
                }
                """;
        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(heartLike))
                .andExpect(status().isOk());

        // Second user likes with LIKE
        final String likeLike = """
                {
                    "reactionType": "LIKE"
                }
                """;
        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId, secondLikerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(likeLike))
                .andExpect(status().isOk());

        // Post creator likes with AMAZE
        final String amazeLike = """
                {
                    "reactionType": "AMAZE"
                }
                """;
        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId, postCreatorUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(amazeLike))
                .andExpect(status().isOk());

        // Get aggregated stats
        final var statsResult = mockMvc.perform(get("/v1/posts/{postId}/likes/stats", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final var statsJson = objectMapper.readTree(statsResult.getResponse().getContentAsString());

        softly.assertThat(statsJson.get("totalLikesCount").asInt())
                .as("Total likes count should be 3")
                .isEqualTo(3);

        softly.assertThat(statsJson.get("heartCount").asInt())
                .as("Heart count should be 1")
                .isEqualTo(1);

        softly.assertThat(statsJson.get("likeCount").asInt())
                .as("Like count should be 1")
                .isEqualTo(1);

        softly.assertThat(statsJson.get("amazeCount").asInt())
                .as("Amaze count should be 1")
                .isEqualTo(1);
    }

    @Test
    void changeReaction_shouldUpdateStats(final SoftAssertions softly) throws Exception {
        final String postId = createTestBlogPost();

        // Initial like with HEART
        final String heartLike = """
                {
                    "reactionType": "HEART"
                }
                """;
        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(heartLike))
                .andExpect(status().isOk());

        // Change to SAD
        final String sadLike = """
                {
                    "reactionType": "SAD"
                }
                """;
        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sadLike))
                .andExpect(status().isOk());

        // Get stats
        final var statsResult = mockMvc.perform(get("/v1/posts/{postId}/likes/stats", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final var statsJson = objectMapper.readTree(statsResult.getResponse().getContentAsString());

        softly.assertThat(statsJson.get("totalLikesCount").asInt())
                .as("Total likes count should remain 1 after changing reaction")
                .isEqualTo(1);

        softly.assertThat(statsJson.get("heartCount").asInt())
                .as("Heart count should be 0 after switching")
                .isEqualTo(0);

        softly.assertThat(statsJson.get("sadCount").asInt())
                .as("Sad count should be 1 after switching")
                .isEqualTo(1);
    }

    @Test
    void unlikePost_shouldDecrementStats(final SoftAssertions softly) throws Exception {
        final String postId = createTestBlogPost();

        // Like the post
        final String likeRequest = """
                {
                    "reactionType": "ANGRY"
                }
                """;
        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(likeRequest))
                .andExpect(status().isOk());

        // Unlike the post
        mockMvc.perform(delete("/v1/posts/{postId}/likes/users/{userId}", postId, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Get stats
        final var statsResult = mockMvc.perform(get("/v1/posts/{postId}/likes/stats", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final var statsJson = objectMapper.readTree(statsResult.getResponse().getContentAsString());

        softly.assertThat(statsJson.get("totalLikesCount").asInt())
                .as("Total likes count should be 0 after unlike")
                .isEqualTo(0);

        softly.assertThat(statsJson.get("angryCount").asInt())
                .as("Angry count should be 0 after unlike")
                .isEqualTo(0);
    }

    @Test
    void getLikesForPost_shouldReturnAllLikes(final SoftAssertions softly) throws Exception {
        final String postId = createTestBlogPost();

        // Multiple users like the post
        final String heart = """
                {"reactionType": "HEART"}
                """;
        final String surprised = """
                {"reactionType": "SURPRISED"}
                """;

        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(heart))
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId, secondLikerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surprised))
                .andExpect(status().isOk());

        // Get all likes for post
        final var result = mockMvc.perform(get("/v1/posts/{postId}/likes", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final var likesArray = objectMapper.readTree(result.getResponse().getContentAsString());

        softly.assertThat(likesArray.isArray())
                .as("Response should be an array")
                .isTrue();

        softly.assertThat(likesArray.size())
                .as("Post should have 2 likes")
                .isEqualTo(2);
    }

    @Test
    void getLikesByUser_shouldReturnAllUserLikes(final SoftAssertions softly) throws Exception {
        // Create multiple posts and like them
        final String postId1 = createTestBlogPost();
        final String postId2 = createTestBlogPost();

        final String heart = """
                {"reactionType": "HEART"}
                """;
        final String like = """
                {"reactionType": "LIKE"}
                """;

        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId1, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(heart))
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/posts/{postId}/likes/users/{userId}", postId2, likerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(like))
                .andExpect(status().isOk());

        // Get all likes by user
        final var result = mockMvc.perform(get("/v1/posts/likes/users/{userId}", likerUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final var likesArray = objectMapper.readTree(result.getResponse().getContentAsString());

        softly.assertThat(likesArray.isArray())
                .as("Response should be an array")
                .isTrue();

        softly.assertThat(likesArray.size())
                .as("User should have 2 likes")
                .isEqualTo(2);
    }
}

