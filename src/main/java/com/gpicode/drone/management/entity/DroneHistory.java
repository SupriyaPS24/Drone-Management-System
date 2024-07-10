package com.gpicode.drone.management.entity;

import com.gpicode.drone.management.dto.MoveDroneResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Setter
@Getter
public class DroneHistory {

    private UUID droneId;
    private List<MoveDroneResponse> states;

    public DroneHistory() {
        states = new ArrayList<>();
    }
}
