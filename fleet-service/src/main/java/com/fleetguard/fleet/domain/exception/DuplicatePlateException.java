package com.fleetguard.fleet.domain.exception;

public class DuplicatePlateException extends RuntimeException {

    public DuplicatePlateException(String plate) {
        super("A vehicle with plate '" + plate + "' already exists");
    }
}
