package com.twoday.spaceshipparking.dto;

import com.twoday.spaceshipparking.dao.ParkingPlace;
import com.twoday.spaceshipparking.dao.SpaceShip;
import com.twoday.spaceshipparking.dao.SpaceShipUser;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
