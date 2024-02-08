package com.twoday.spaceshipparking.usecases;

import com.twoday.spaceshipparking.service.ParkingService;
import com.twoday.spaceshipparking.dao.Parking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetParkedSpaceShipUseCase {
    private final ParkingService parkingService;

    public Parking doGet(String parkingId) {
        return parkingService.get(parkingId);
    }
}
