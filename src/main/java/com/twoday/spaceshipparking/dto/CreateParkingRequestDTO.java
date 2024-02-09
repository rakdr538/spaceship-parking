package com.twoday.spaceshipparking.dto;

import com.twoday.spaceshipparking.dao.ParkingPlace;
import com.twoday.spaceshipparking.dao.SpaceShip;
import com.twoday.spaceshipparking.dao.SpaceShipUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateParkingRequestDTO {
    @NotNull
    @Valid
    private ParkingPlace parkingPlace;

    @NotNull
    @Valid
    private SpaceShip spaceShip;

    @NotNull
    @Valid
    private SpaceShipUser spaceShipUser;
}
