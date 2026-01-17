package com.famevently.monolith.userrating;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;

import com.jetbrains.exported.JBRApi.Service;

@Service
public class UserRatingService {
    private final UserRatingRepository repository;
    private final UserRatingSummaryRepository summaryRepository;

    public UserRatingService(UserRatingRepository repository, UserRatingSummaryRepository summaryRepository) {
        this.repository = repository;
        this.summaryRepository = summaryRepository;
    }

    public void rateUser(UserRatingRequest request) throws UserAlreadyRatedException {
        if (request.ratedByUserId() == request.ratedUserId()) {
            throw new IllegalArgumentException("User cannot rate themselves");
        }

        try {
            repository.insert(
                request.ratedUserId(),
                request.ratedByUserId(),
                request.eventId(),
                request.score(),
                OffsetDateTime.now()
            );
        } catch (DuplicateKeyException ex) {
            throw new UserAlreadyRatedException();
        }
    }

    public List<UserRating> getRatingsByUser(long userId) {
        return repository.findByUserId(userId);
    }

    public List<UserRating> getRatingsByEvent(long eventId) {
        return repository.findByEventId(eventId);
    }

    public UserRatingSummary getRatingSummaryByUser(long ratedUserId) {
        return summaryRepository.getUserRatingSummary(ratedUserId);
    }

    public UserRatingSummary getRatingSummaryByEvent(long eventId) {
        return summaryRepository.getEventRatingSummary(eventId);
    }

    public boolean hasUserRated(long ratedUserId, long ratedByUserId, long eventId) {
        return repository.findByRatedAndRater(ratedUserId, ratedByUserId, eventId).isPresent();
    }
}
