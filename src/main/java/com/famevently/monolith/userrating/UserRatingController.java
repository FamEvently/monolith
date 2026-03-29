package com.famevently.monolith.userrating;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/v1/user-rating")
public class UserRatingController {
    private final UserRatingService ratingService;

    public UserRatingController(UserRatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public void rateUser(@RequestBody UserRatingRequest request) throws UserAlreadyRatedException {
        ratingService.rateUser(request);
    }
 
    @GetMapping("/user/{userId}")
    public List<UserRating> getRatingsByUser(@PathVariable long userId) {
        return ratingService.getRatingsByUser(userId);
    }

    @GetMapping("/user/{userId}/summary")
    public UserRatingSummary getRatingSummaryByUser(@PathVariable long userId) {
        return ratingService.getRatingSummaryByUser(userId);
    }

    @GetMapping("/event/{eventId}")
    public List<UserRating> getRatingsByEvent(@PathVariable long eventId) {
        return ratingService.getRatingsByEvent(eventId);
    }

    @GetMapping("/event/{eventId}/summary")
    public UserRatingSummary getRatingSummaryByEvent(@PathVariable long eventId) {
        return ratingService.getRatingSummaryByEvent(eventId);
    }

    @GetMapping("/hasRated")
    public Boolean hasUserRated(
            @RequestParam long ratedUserId,
            @RequestParam long ratedByUserId,
            @RequestParam long eventId
    ) {
        return ratingService.hasUserRated(ratedUserId, ratedByUserId, eventId);
    }
}