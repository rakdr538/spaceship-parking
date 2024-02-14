package com.twoday.spaceshipparking.service;

import com.twoday.spaceshipparking.dao.Parking;

import java.util.List;

public interface ParkingService {
    Parking createOrUpdate(Parking parking);
    Parking get(String parkingId);
    List<Parking> getAllOccupiedSpots();
}
