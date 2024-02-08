package com.twoday.spaceshipparking;

import com.twoday.spaceshipparking.dao.Parking;
import com.twoday.spaceshipparking.dto.CreateParkingRequestDTO;
import com.twoday.spaceshipparking.usecases.GetParkedSpaceShipUseCase;
import com.twoday.spaceshipparking.usecases.ParkSpaceShipUseCase;
import com.twoday.spaceshipparking.usecases.VacateParkingSpotUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parkings")
public class SpaceShipParkingController {
    private final ParkSpaceShipUseCase parkSpaceShipUseCase;
    private final GetParkedSpaceShipUseCase getParkedSpaceShipUseCase;
    private final VacateParkingSpotUseCase vacateParkingSpotUseCase;

    @PostMapping
    public ResponseEntity<Parking> parkSpaceShip (@Valid @RequestBody CreateParkingRequestDTO parkingRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkSpaceShipUseCase.doPark(parkingRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parking> getParkedSpaceShip (@NotBlank @PathVariable String id){
        return ResponseEntity.ok(getParkedSpaceShipUseCase.doGet(id));
    }

    @GetMapping("/vacate/{id}")
    public ResponseEntity<Parking> vacateParkedSpaceShip(@NotBlank @PathVariable String id) {
        return ResponseEntity.ok(vacateParkingSpotUseCase.doVacate(id));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<Parking>> getAllParkingsByUser (@NotBlank @PathVariable String id) {
        // this is to demo that this functionality can be extended.
        // here you can get all Parkings for given user.
     return null;
    }

    @GetMapping("/space-ship/{regNo}")
    public ResponseEntity<List<Parking>> getAllParkingsBySpaceShipRegNo (@NotBlank @PathVariable String regNo) {
        // this is to demo that this functionality can be extended.
        // here you can get all parkings for given spaceship with registration number.
        return null;
    }
}
