package com.twoday.spaceshipparking;

import com.twoday.spaceshipparking.dao.Parking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParkingRepository extends MongoRepository<Parking, String> {

    List<Parking> findAllByParkingEndedAtIsNullAndDurationInHoursIsNull();
}
