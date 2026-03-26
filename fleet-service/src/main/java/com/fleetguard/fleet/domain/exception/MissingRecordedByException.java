package com.fleetguard.fleet.domain.exception;

public class MissingRecordedByException extends RuntimeException {

    public MissingRecordedByException() {
        super("Recorded by (driver) is required");
    }
}
