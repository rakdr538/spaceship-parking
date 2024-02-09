package com.twoday.spaceshipparking.usecases;

import com.twoday.spaceshipparking.dao.Parking;
import com.twoday.spaceshipparking.dto.CreateParkingRequestDTO;
import com.twoday.spaceshipparking.dto.mappers.ParkingRequestToParkingMapper;
import com.twoday.spaceshipparking.exceptions.ParkingProhibitedException;
import com.twoday.spaceshipparking.service.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@RequiredArgsConstructor
public class ParkSpaceShipUseCase {
    private final ParkingService parkingService;

    public Parking doPark(@Valid CreateParkingRequestDTO parkingRequest) {
        parkingService
                .getAllOccupiedSpots()
                .forEach(parked -> isDoubleParking(parkingRequest, parked));

        return parkingService.createOrUpdate(ParkingRequestToParkingMapper.mapCreateReqToParking(parkingRequest));
    }

    private void isDoubleParking(CreateParkingRequestDTO parkingRequest, Parking parked)
            throws ParkingProhibitedException {
        if (parked.getParkingPlace().getFloor().equals(parkingRequest.getParkingPlace().getFloor())
                && parked.getParkingPlace().getPlot().equals(parkingRequest.getParkingPlace().getPlot())) {
            throw new ParkingProhibitedException(parkingRequest.getParkingPlace().getFloor(),
                    parkingRequest.getParkingPlace().getPlot());
        }
    }
}
