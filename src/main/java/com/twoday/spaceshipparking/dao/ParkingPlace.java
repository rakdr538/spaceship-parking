package com.twoday.spaceshipparking.dao;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ParkingPlace {
    @NotNull (message = "Parking floor cannot be null.")
    @Min(1)
    @Max(3)
    private Integer floor;

    @NotNull (message = "Parking plot cannot be null.")
    @Min(1)
    @Max(15)
    private Integer plot;
}
