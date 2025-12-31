package com.famevently.monolith.event;

import java.time.LocalDate;

public record CreateEventRequest(
        String overview,
        String category,
        LocalDate eventDate,
        String address,
        String location,
        String additionalInfo,
        Integer minAge,
        Integer maxAge,
        Boolean adultsOnly
) {
}
