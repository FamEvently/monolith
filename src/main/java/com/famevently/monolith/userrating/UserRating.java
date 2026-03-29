package com.famevently.monolith.userrating;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UserRating(
    long ratingId,
    long ratedUserId,
    long ratedByUserId,
    long eventId,
    BigDecimal score,
    OffsetDateTime createdAt
) {}
