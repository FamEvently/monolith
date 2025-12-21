package com.famevently.monolith.event;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record Event(
        Long eventId,
        Long userId,
        String overview,
        String eventCategory,
        LocalDate date,
        String address,
        String location,
        String additionalInfo,
        Integer minAge,
        Integer maxAge,
        Boolean adultsOnly,
        OffsetDateTime createdAt)
{
}
