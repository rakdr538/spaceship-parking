package com.twoday.spaceshipparking.dao;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class SpaceShip {
    private String name;
    @NotBlank(message = "Valid registration number must be provided.")
    @Size(min=6)
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Invalid registration number")
    private String registrationNumber;
}
