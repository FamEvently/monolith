package com.famevently.monolith.customer;

import com.famevently.monolith.cache.CacheEntity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@CacheEntity(cacheName = "userCoreInfo", ttl = 30, timeUnit = TimeUnit.MINUTES)
public record UserCoreInfo(
        long userId,
        String email,
        String firstName,
        String lastName,
        String language,
        String gender,
        LocalDate birthday,
        String country,
        boolean isWhitelisted,
        OffsetDateTime createdAt
) {
}
