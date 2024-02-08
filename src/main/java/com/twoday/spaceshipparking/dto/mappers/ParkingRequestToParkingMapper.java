package com.twoday.spaceshipparking.dto.mappers;

import com.twoday.spaceshipparking.dao.Parking;
import com.twoday.spaceshipparking.dto.CreateParkingRequestDTO;

import java.time.LocalDateTime;

public class ParkingRequestToParkingMapper {

    public static Parking mapCreateReqToParking(CreateParkingRequestDTO parkingRequestDTO) {
        return Parking.builder()
                .parkingPlace(parkingRequestDTO.getParkingPlace())
                .spaceShipUser(parkingRequestDTO.getSpaceShipUser())
                .spaceShip(parkingRequestDTO.getSpaceShip())
                .parkingStartedAt(LocalDateTime.now())
                .build();
    }
}
