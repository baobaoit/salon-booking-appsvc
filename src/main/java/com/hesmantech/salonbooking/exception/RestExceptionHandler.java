package com.hesmantech.salonbooking.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> keyMessages = Map.of(
                "groups_name_key", "The group name you've chosen is already taken. Please select a different name.",
                "users_phone_number_key", "The phone number you've chosen is already taken. Please select a different phone number."
        );

        final String exMessage = ex.getMostSpecificCause().getMessage();
        String detailMessage = keyMessages.entrySet().stream()
                .filter(entry -> exMessage.contains(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(exMessage);


        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detailMessage);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ProblemDetail handleBadSqlGrammarException(BadSqlGrammarException ignored) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Bad SQL grammar");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ResponseEntity<Object> errorResponse = super.handleMethodArgumentNotValid(ex, headers, status, request);

        if (errorResponse == null) {
            return null;
        }

        StringBuilder errorDetails = new StringBuilder();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errorDetails.append(fieldName).append(' ').append(errorMessage).append(", ");
                });
        errorDetails.setLength(errorDetails.length() - 2);

        ProblemDetail problemDetail = (ProblemDetail) errorResponse.getBody();
        if (problemDetail != null) {
            problemDetail.setDetail(errorDetails.toString());
        }

        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException ignored) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "The JWT token has expired");
    }
}
