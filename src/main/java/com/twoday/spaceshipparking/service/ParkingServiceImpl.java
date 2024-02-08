package com.twoday.spaceshipparking.service;

import com.twoday.spaceshipparking.ParkingRepository;
import com.twoday.spaceshipparking.dao.Parking;
import com.twoday.spaceshipparking.exceptions.ParkingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService{
    private final ParkingRepository parkingRepository;

    @Override
    @Transactional
    public Parking createOrUpdate(Parking parking) {
        return parkingRepository.save(parking);
    }

    @Override
    public Parking get(String parkingId) {
        return parkingRepository
                .findById(parkingId)
                .orElseThrow(() -> new ParkingNotFoundException(parkingId));
    }

    @Override
    public List<Parking> getAllOccupiedSpots() {
        return parkingRepository.findAllByParkingEndedAtIsNullAndDurationInHoursIsNull();
    }
}
