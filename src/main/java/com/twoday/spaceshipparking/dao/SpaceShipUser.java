package com.twoday.spaceshipparking.dao;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class SpaceShipUser {
    @Id
    private String user_id;
    private String name;
}
