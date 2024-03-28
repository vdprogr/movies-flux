package com.reactivespring.globalerrorhandler;

import com.reactivespring.exception.MoviesInfoClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {
    @ExceptionHandler(MoviesInfoClientException.class)
    public ResponseEntity<String> handlerClientException(MoviesInfoClientException ex) {
        log.error("Excepton in handleClientException: {} ", ex.getMessage());

        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handlerRuntimeException(RuntimeException ex) {
        log.error("Excepton in handleRuntimeException: {} ", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}