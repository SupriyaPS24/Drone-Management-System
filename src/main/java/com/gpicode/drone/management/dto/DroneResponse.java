package com.gpicode.drone.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DroneResponse {

    @JsonProperty("droneId")
    private UUID droneId;

    @JsonProperty("xCoordinate")
    private int xCoordinate;

    @JsonProperty("yCoordinate")
    private int yCoordinate;

    @JsonProperty("direction")
    private String direction;

}
