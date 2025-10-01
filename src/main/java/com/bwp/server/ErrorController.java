package com.bwp.server;

import com.quiptmc.core.exceptions.QuiptApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(QuiptApiException.class)
    public ResponseEntity<ApiErrorResponse> handleQuiptApiException(
            QuiptApiException ex, HttpServletRequest httpServletRequest) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(501, ex.getMessage());
        return ResponseEntity.status(HttpStatus.valueOf(501)).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
    }

    @ExceptionHandler({JSONException.class})
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(
            JSONException ex, HttpServletRequest httpServletRequest) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(500, "Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
    }

    public record ApiErrorResponse(int code, String message) {

    }

}
