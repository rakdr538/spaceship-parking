package com.twoday.spaceshipparking.dao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpaceShip {
    private String name;

    @NotBlank(message = "Valid registration number must be provided.")
    @Size(min=6)
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Invalid registration number")
    private String registrationNumber;
}
