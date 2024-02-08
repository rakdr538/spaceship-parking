package com.twoday.spaceshipparking.dto;

import com.twoday.spaceshipparking.dao.ParkingPlace;
import com.twoday.spaceshipparking.dao.SpaceShip;
import com.twoday.spaceshipparking.dao.SpaceShipUser;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreateParkingRequestDTO {
    @NotNull
    private ParkingPlace parkingPlace;

    @NotNull
    private SpaceShip spaceShip;

    @NotNull
    private SpaceShipUser spaceShipUser;
}
