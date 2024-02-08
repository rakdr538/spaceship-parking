package com.twoday.spaceshipparking.usecases;

import com.twoday.spaceshipparking.dao.Parking;
import com.twoday.spaceshipparking.exceptions.ExistingRecordException;
import com.twoday.spaceshipparking.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VacateParkingSpotUseCase {
    private final ParkingService parkingService;

    public Parking doVacate(String parkingId) {
        var parkedShip = parkingService.get(parkingId);

        // do not update existing records for history purposes
        if (!ObjectUtils.isEmpty(parkedShip.getParkingEndedAt())
                && !ObjectUtils.isEmpty(parkedShip.getDurationInHours()))
        {
            throw new ExistingRecordException(parkingId);
        }

        final var currentTime = LocalDateTime.now();
        final Duration duration = Duration.between(parkedShip.getParkingStartedAt(), currentTime);

        if (duration.toMinutes() > 1 && duration.toMinutes() < 180) {
            // pay hourly rate starting from a minute until 180 minutes (3 hours)
            calculateHourlyRate(duration, parkedShip);
        } else if (duration.toHours() > 3) {
            // pay daily rate starting from 3 hours
            calculateDailyRate(duration, parkedShip);
        } else {
            // Do not need to pay if you are parked under a minute
            parkedShip.setDurationInHours(0L);
            parkedShip.setTotalPriceInSek(0F);
        }

        parkedShip.setParkingEndedAt(currentTime);
        return parkingService.createOrUpdate(parkedShip);
    }

    private void calculateHourlyRate(Duration duration, Parking parkedShip) {
        switch ((int) (duration.toMinutes() / 60)) {
            case 0 -> {
                parkedShip.setTotalPriceInSek(15F);
                parkedShip.setDurationInHours(1L);
            }
            case 1 -> {
                parkedShip.setTotalPriceInSek(15F * 2);
                parkedShip.setDurationInHours(2L);
            }
            case 2 -> {
                parkedShip.setTotalPriceInSek(15F * 3);
                parkedShip.setDurationInHours(3L);
            }
        }
    }

    private void calculateDailyRate(Duration duration, Parking parkedShip) {
        long daysOfParking = duration.toHours() / 24;
        float additionalMinutesOfParking = ((float)duration.toHours() % 24) * 60;

        float calculatedPrice = daysOfParking * 50;

        if (additionalMinutesOfParking > 180) {
            calculatedPrice = calculatedPrice + 50;
        } else {
            switch ((int) (additionalMinutesOfParking / 60)) {
                case 0 -> calculatedPrice = calculatedPrice + 15;
                case 1 -> calculatedPrice = calculatedPrice + (15 * 2);
                case 2 -> calculatedPrice = calculatedPrice + (15 * 3);
            }
        }

        parkedShip.setDurationInHours(duration.toHours());
        parkedShip.setTotalPriceInSek(calculatedPrice);
    }
}
