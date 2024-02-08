package com.twoday.spaceshipparking.exceptions;

public class ParkingProhibitedException extends RuntimeException {
    public ParkingProhibitedException(Integer floor, Integer plot) {
        super(String.format("Cannot double park at floor:%d & plot:%d]", floor, plot));
    }
}
