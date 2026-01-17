package com.famevently.monolith.userrating;

import java.math.BigDecimal;

public record UserRatingRequest(
        long ratedUserId,
        long ratedByUserId,
        long eventId,
        BigDecimal score
) {}
