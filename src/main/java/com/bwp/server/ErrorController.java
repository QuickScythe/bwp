package com.bwp.server;

import com.quiptmc.core.exceptions.QuiptApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
        return new ApiErrorResponse(500, ex.getMessage(), sb.toString(), causeBuilder.toString());
    }

    @ExceptionHandler({JSONException.class})
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(
            JSONException ex, HttpServletRequest httpServletRequest) {
        ApiErrorResponse apiErrorResponse = printException(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
    }

    public record ApiErrorResponse(int code, String message, String stackTrack, String cause) {

    }

}
