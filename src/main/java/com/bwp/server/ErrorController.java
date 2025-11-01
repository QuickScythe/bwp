package com.bwp.server;

import com.quiptmc.core.exceptions.QuiptApiException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global exception handler for MVC and REST controllers.
 *
 * Captures common exceptions and returns a consistent JSON response body (ApiErrorResponse)
 * with HTTP status codes appropriate to the error. This improves client diagnostics
 * and avoids leaking stack traces via default HTML error pages.
 */
@ControllerAdvice
public class ErrorController {

    /**
     * Handles QuiptMC-specific API exceptions and returns a 500 error with JSON body.
     *
     * @param ex the thrown QuiptApiException
     * @param httpServletRequest request context (unused but available for future needs)
     * @return ResponseEntity with HTTP 500 and structured ApiErrorResponse
     */
    @ExceptionHandler(QuiptApiException.class)
    public ResponseEntity<ApiErrorResponse> handleQuiptApiException(
            QuiptApiException ex, HttpServletRequest httpServletRequest) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
    }

    /**
     * Catches any unhandled Exception and returns a 500 response with details.
     *
     * @param ex the thrown exception
     * @param httpServletRequest request context (unused)
     * @return ResponseEntity with HTTP 500 and structured ApiErrorResponse
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest httpServletRequest) {
        ApiErrorResponse apiErrorResponse = printException(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
    }

    /**
     * Returns the status and reason from a ResponseStatusException as a JSON response.
     *
     * @param ex the ResponseStatusException thrown by controllers/services
     * @param httpServletRequest request context (unused)
     * @return ResponseEntity with status from the exception and ApiErrorResponse containing the reason
     */
    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(ex.getStatusCode().value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiErrorResponse(
                        ex.getStatusCode().value(),
                        ex.getReason(),
                        null,
                        null
                ));
    }

    /**
     * Builds a detailed ApiErrorResponse from an Exception, including stack trace and causes.
     *
     * @param ex the exception to serialize
     * @return structured ApiErrorResponse with code, message, stackTrace and cause
     */
    private ApiErrorResponse printException(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        StringBuilder causeBuilder = new StringBuilder();
        Throwable cause = ex.getCause();
        while (cause != null) {
            causeBuilder.append("Caused by: ").append(cause.toString()).append("\n");
            for (StackTraceElement element : cause.getStackTrace()) {
                causeBuilder.append("\tat ").append(element.toString()).append("\n");
            }
            cause = cause.getCause();
        }
        int code = ex instanceof ResponseStatusException resp ? resp.getStatusCode().value() : HttpStatus.INTERNAL_SERVER_ERROR.value();

        return new ApiErrorResponse(code, ex.getMessage(), sb.toString(), causeBuilder.toString());
    }

    /**
     * Handles JSON parsing errors by returning a 404 NOT FOUND with details.
     * Note: In some flows a malformed JSON may indicate missing handlers or bad inputs.
     *
     * @param ex the JSONException thrown during parsing
     * @param httpServletRequest request context (unused)
     * @return ResponseEntity with HTTP 404 and structured ApiErrorResponse
     */
    @ExceptionHandler({JSONException.class})
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(
            JSONException ex, HttpServletRequest httpServletRequest) {
        ApiErrorResponse apiErrorResponse = printException(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
    }

    /**
     * Standard JSON error payload returned by the global ErrorController.
     *
     * @param code       HTTP status code
     * @param message    human-readable error message
     * @param stackTrack stack trace of the exception (may be null in some handlers)
     * @param cause      nested causes combined as a string (may be null)
     */
    public record ApiErrorResponse(int code, String message, @Nullable String stackTrack,@Nullable String cause) {

    }

}
