package com.twoday.spaceshipparking.dao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParkingPlace {
    @NotNull(message = "Parking floor cannot be null.")
    @Min(1)
    @Max(3)
    private Integer floor;

    @NotNull (message = "Parking plot cannot be null.")
    @Min(1)
    @Max(15)
    private Integer plot;
}
