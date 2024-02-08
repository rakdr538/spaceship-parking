package com.twoday.spaceshipparking.exceptions;

public class ExistingRecordException extends RuntimeException {
    public ExistingRecordException(String parkingId) {
        super(String.format("Cannot update existing parking(%s) that has ended", parkingId));
    }
}
