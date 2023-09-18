package com.springframework.spring6restmvc.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomErrorController {

    @ExceptionHandler
    ResponseEntity handleJPAViolations(TransactionSystemException exception) {
        ResponseEntity.BodyBuilder responseEntity = ResponseEntity.badRequest();

        // if it is a validation error: return with an error message
        if (exception.getCause().getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException violationException = (ConstraintViolationException) exception.getCause().getCause();

            List errorList = violationException.getConstraintViolations()
                    .stream()
                    .map(constraintViolation -> {
                        Map<String, String> errMap = new HashMap<>();
                        errMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                        return errMap;
                    })
                    .collect(Collectors.toList());

            return responseEntity.body(errorList);
        }

        // returns BAD REQUEST response for any error except ConstraintViolation, without body
        return responseEntity.build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleBindingErrors(MethodArgumentNotValidException exception) {
        List errorList = exception.getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errorList);
    }
}
