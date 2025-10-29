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

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(QuiptApiException.class)
    public ResponseEntity<ApiErrorResponse> handleQuiptApiException(
            QuiptApiException ex, HttpServletRequest httpServletRequest) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest httpServletRequest) {
        ApiErrorResponse apiErrorResponse = printException(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
    }

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

    @ExceptionHandler({JSONException.class})
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(
            JSONException ex, HttpServletRequest httpServletRequest) {
        ApiErrorResponse apiErrorResponse = printException(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
    }

    public record ApiErrorResponse(int code, String message, @Nullable String stackTrack,@Nullable String cause) {

    }

}
