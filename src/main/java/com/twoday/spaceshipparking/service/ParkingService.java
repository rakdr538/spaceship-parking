package com.twoday.spaceshipparking.service;

import com.twoday.spaceshipparking.dao.Parking;

import java.util.List;

public interface ParkingService {
    public Parking createOrUpdate(Parking parking);
    public Parking get(String parkingId);
    public List<Parking> getAllOccupiedSpots();
}
