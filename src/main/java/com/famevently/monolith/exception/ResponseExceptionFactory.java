package com.famevently.monolith.exception;

import java.util.HashMap;
import java.util.Map;

final class ResponseExceptionFactory {
    private static final String TYPE_PROPERTY = "type";

    private static final String BUSINESS_EXCEPTION = "BusinessException";

    static GlobalExceptionResponse businessException(String type)
    {
        return customException(BUSINESS_EXCEPTION, type);
    }

    static GlobalExceptionResponse customException(String code, String type)
    {
        final Map<String, Object> context = new HashMap<>(1);

        if (type != null)
        {
            context.put(TYPE_PROPERTY, type);
        }

        return new GlobalExceptionResponse(code, context);
    }
}
