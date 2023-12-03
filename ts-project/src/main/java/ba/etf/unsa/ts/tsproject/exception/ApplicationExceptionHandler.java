package ba.etf.unsa.ts.tsproject.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ErrorDetails> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ErrorDetails> errorResponses = new ArrayList<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            ErrorDetails errorResponse = new ErrorDetails(LocalDateTime.now(),fieldError.getField(),fieldError.getDefaultMessage());
            errorResponses.add(errorResponse);
        }
        return errorResponses;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ErrorDetails> handleConstraintViolationException(ConstraintViolationException ex) {
        List<ErrorDetails> errorResponses = new ArrayList<>();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : constraintViolations) {
            ErrorDetails errorResponse = new ErrorDetails(LocalDateTime.now(), violation.getPropertyPath().toString(), violation.getMessage());
            errorResponses.add(errorResponse);
        }
        return errorResponses;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ErrorDetails> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        List<ErrorDetails> errorResponses = new ArrayList<>();
        String message = ex.getMessage();
        if (message.contains("Cannot deserialize value of type `java.lang.Boolean`")) {
            errorResponses.add(new ErrorDetails(LocalDateTime.now(), "nezavisna", "Invalid value for Boolean property: " + message));
        } else {
            errorResponses.add(new ErrorDetails(LocalDateTime.now(), "Request body", "Invalid request body: " + message));
        }
        return errorResponses;
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ErrorDetails> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        List<ErrorDetails> errorResponses = new ArrayList<>();

        ErrorDetails errorResponse = new ErrorDetails(LocalDateTime.now(), "Unique key", ex.getMessage());
        errorResponses.add(errorResponse);

        return errorResponses;
    }
}