package com.gpicode.drone.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoveDroneRequest {

    @NotNull(message = "xCoordinate is required")
    @JsonProperty("xCoordinate")
    private Integer xCoordinate;

    @NotNull(message = "yCoordinate is required")
    @JsonProperty("yCoordinate")
    private Integer yCoordinate;
}
