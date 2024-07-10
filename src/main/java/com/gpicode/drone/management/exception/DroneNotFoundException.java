package com.gpicode.drone.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DroneNotFoundException extends Throwable{
    public DroneNotFoundException(String message) {
        super(message);
    }
}
