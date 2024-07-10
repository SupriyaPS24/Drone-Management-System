package com.gpicode.drone.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterDroneRequest {

    @NotNull(message = "xCoordinate is required")
    @JsonProperty("xCoordinate")
    private Integer xCoordinate;

    @NotNull(message = "yCoordinate is required")
    @JsonProperty("yCoordinate")
    private Integer yCoordinate;

    @NotNull(message = "direction is required")
    @Pattern(regexp = "(?i)NORTH|EAST|WEST|SOUTH", message = "Direction must be one of North, East, West, South")
    @JsonProperty("direction")
    private String direction;

}
    