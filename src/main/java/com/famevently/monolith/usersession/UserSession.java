package com.famevently.monolith.usersession;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserSession(
        @JsonProperty("id")
        String sessionId,
        long userId,
        boolean isAdmin)
{
}
