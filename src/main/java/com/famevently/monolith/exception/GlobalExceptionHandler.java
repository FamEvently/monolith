package com.famevently.monolith.exception;

import com.famevently.monolith.attendance.CannotAttendOwnEventException;
import com.famevently.monolith.customer.CustomerNotFoundException;
import com.famevently.monolith.event.EventNotFoundException;
import com.famevently.monolith.login.InvalidGoogleCredentials;
import com.famevently.monolith.password.InvalidCredentials;
import com.famevently.monolith.postlikes.PostNotFoundException;
import com.famevently.monolith.registration.EmailAlreadyUsedException;
import com.famevently.monolith.usersession.InvalidSessionException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    HttpServletRequest request;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalExceptionResponse> handleException(final Exception e)
    {
        logger.error("Unhandled exception: {}", e.getMessage(), e);

        final String code = "InternalError";
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, ResponseExceptionFactory.customException(code, null));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<GlobalExceptionResponse> handleThrowable(final Throwable e)
    {
        logger.error("Critical service error: {}", e.getMessage(), e);

        final String code = "InternalError";
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, ResponseExceptionFactory.customException(code, null));
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<GlobalExceptionResponse> handleIllegalStateException(final IllegalStateException e)
    {
        return respond(HttpStatus.BAD_REQUEST, ResponseExceptionFactory.businessException("IllegalStateException"));
    }

    @ExceptionHandler({InvalidGoogleCredentials.class})
    public ResponseEntity<GlobalExceptionResponse> handleInvalidGoogleCredentials(final InvalidGoogleCredentials e)
    {
        logger.warn("Invalid Google credentials: {}", e.getMessage());
        return respond(HttpStatus.UNAUTHORIZED, ResponseExceptionFactory.businessException(e.getMessage()));
    }

    @ExceptionHandler({CustomerNotFoundException.class})
    public ResponseEntity<GlobalExceptionResponse> handleCustomerNotFound(final CustomerNotFoundException e)
    {
        logger.info("Customer not found: {}", e.getMessage());
        return respond(HttpStatus.NOT_FOUND, ResponseExceptionFactory.businessException(e.getMessage()));
    }

    @ExceptionHandler({EmailAlreadyUsedException.class})
    public ResponseEntity<GlobalExceptionResponse> handleEmailAlreadyUsed(final EmailAlreadyUsedException e)
    {
        logger.info("Email already used: {}", e.getMessage());
        return respond(HttpStatus.UNAUTHORIZED, ResponseExceptionFactory.businessException(e.getMessage()));
    }

    @ExceptionHandler({PostNotFoundException.class})
    public ResponseEntity<GlobalExceptionResponse> handlePostNotFound(final PostNotFoundException e)
    {
        logger.error("Post not found: {}", e.getPostId());
        return respond(HttpStatus.BAD_REQUEST, ResponseExceptionFactory.businessException(e.getMessage()));
    }

    @ExceptionHandler({InvalidCredentials.class})
    public ResponseEntity<GlobalExceptionResponse> handleInvalidCredentials(final InvalidCredentials e)
    {
        logger.warn("Invalid credentials for user: {}", e.getEmail());
        return respond(HttpStatus.UNAUTHORIZED, ResponseExceptionFactory.businessException(e.getMessage()));
    }

    @ExceptionHandler({InvalidSessionException.class})
    public ResponseEntity<GlobalExceptionResponse> handleInvalidSessionException(final InvalidSessionException e)
    {
        logger.warn("Invalid session");
        return respond(HttpStatus.UNAUTHORIZED, ResponseExceptionFactory.businessException(e.getMessage()));
    }

    @ExceptionHandler({CannotAttendOwnEventException.class})
    public ResponseEntity<GlobalExceptionResponse> handleCannotAttendOwnEvent(final CannotAttendOwnEventException e)
    {
        logger.info("User attempted to attend own event");
        return respond(HttpStatus.BAD_REQUEST, ResponseExceptionFactory.businessException(e.getMessage()));
    }

    @ExceptionHandler({EventNotFoundException.class})
    public ResponseEntity<GlobalExceptionResponse> handleEventNotFound(final EventNotFoundException e)
    {
        logger.info("Event not found: {}", e.getEventId());
        return respond(HttpStatus.NOT_FOUND, ResponseExceptionFactory.businessException(e.getMessage()));
    }

    private ResponseEntity<GlobalExceptionResponse> respond(final HttpStatus status,
                                                                                     final GlobalExceptionResponse globalExceptionResponse)
    {
        final boolean suppressStatusCode = Boolean.parseBoolean(request.getParameter("_suppressStatusCode"));
        HttpStatus effectiveStatus = status;
        if (suppressStatusCode)
        {
            globalExceptionResponse.context().put("status", status.value());
            effectiveStatus = HttpStatus.OK;
        }

        return new ResponseEntity<>(globalExceptionResponse, effectiveStatus);
    }
}
