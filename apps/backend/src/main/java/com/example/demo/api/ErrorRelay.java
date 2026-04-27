package com.example.demo.api;

import com.example.demo.api.model.ErrorResponse;
import com.example.demo.exception.SystemFault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorRelay {

    @ExceptionHandler(SystemFault.class)
    public ResponseEntity<ErrorResponse> handleFault(SystemFault fault) {
        ErrorResponse response = new ErrorResponse(fault.getCode(), fault.getMessage(), null);
        
        return ResponseEntity.status(fault.getStatus()).body(response);
    }
}
