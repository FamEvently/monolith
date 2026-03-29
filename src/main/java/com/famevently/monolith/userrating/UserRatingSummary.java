package com.famevently.monolith.userrating;

import java.math.BigDecimal;

public record UserRatingSummary(
    long id,             // ratedUserId or eventId
    long totalRatings,
    BigDecimal averageScore
) {}