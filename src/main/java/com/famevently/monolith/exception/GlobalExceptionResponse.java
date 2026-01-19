package com.famevently.monolith.exception;

import java.util.Map;

public record GlobalExceptionResponse(String code, Map<String, Object> context)
{
}
