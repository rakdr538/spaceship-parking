package com.twoday.spaceshipparking.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@Document(collection="twoday-spaceship-parking")
public class Parking {
    @Id
    private String id;

    @NotNull
    @Valid
    private ParkingPlace parkingPlace;

    @NotNull
    @Valid
    private SpaceShip spaceShip;

    @NotNull
    @Valid
    private SpaceShipUser spaceShipUser;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime parkingStartedAt;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime parkingEndedAt;
    private Float totalPriceInSek;
    // this is always rounded to next closest whole number for easier calculation
    private Long durationInHours;
}
