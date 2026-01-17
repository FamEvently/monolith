package com.famevently.monolith.logintoken;

import java.time.OffsetDateTime;

public record LoginToken(
    long userId,
    String loginToken,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) 
{}
