package com.springframework.spring6restmvc.controller;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice // global exception handler for controllers
public class ExceptionController {

    // If 'any' controller throws a NotFoundException following method will handle it.
    // We have full control over the response. We could add body, header, etc. But here we just return status info.
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException() {

        log.debug("Handling NotFoundException for any Controller - in ExceptionController");

        return ResponseEntity.notFound().build();
    }
}
