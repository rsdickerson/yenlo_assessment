package org.lab.sms.service;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.lab.sms.StockingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StockingException.class)
    public ResponseEntity<ServiceErrorResponse> stockingFailure(Exception ex, WebRequest request) {
        ServiceErrorResponse errors = new ServiceErrorResponse();
        errors.setTimestamp(LocalDateTime.now());
        errors.setError(ex.getMessage());
        errors.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({FileUploadException.class, IOException.class})
    public ResponseEntity<ServiceErrorResponse> fileUploadFailure(Exception ex, WebRequest request) {
        ServiceErrorResponse errors = new ServiceErrorResponse();
        errors.setTimestamp(LocalDateTime.now());
        errors.setError(ex.getMessage());
        errors.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
