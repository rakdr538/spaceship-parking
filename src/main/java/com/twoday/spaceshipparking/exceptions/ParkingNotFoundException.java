package com.twoday.spaceshipparking.exceptions;

public class ParkingNotFoundException extends RuntimeException {
    public ParkingNotFoundException(String parkingId) {
        super("Could not find parking with: " + parkingId);
    }
}
