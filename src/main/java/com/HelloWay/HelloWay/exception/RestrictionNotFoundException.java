package com.HelloWay.HelloWay.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)  // Return 404 status
public class RestrictionNotFoundException extends RuntimeException {
    public RestrictionNotFoundException(String message) {
        super(message);
    }
}

