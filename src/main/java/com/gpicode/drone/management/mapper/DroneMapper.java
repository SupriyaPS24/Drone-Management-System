package com.gpicode.drone.management.mapper;

import com.gpicode.drone.management.dto.DroneResponse;
import com.gpicode.drone.management.dto.MoveDroneRequest;
import com.gpicode.drone.management.dto.RegisterDroneRequest;
import com.gpicode.drone.management.entity.Drone;

public class DroneMapper {

    public static Drone toDroneEntity(RegisterDroneRequest request) {
        Drone drone = new Drone();
        drone.setCoordinateX(request.getXCoordinate());
        drone.setCoordinateY(request.getYCoordinate());
        drone.setDirection(request.getDirection().toUpperCase());
        return drone;
    }

    public static void updateDroneEntity(Drone drone, MoveDroneRequest request) {
        drone.setCoordinateX(request.getXCoordinate());
        drone.setCoordinateY(request.getYCoordinate());
    }

    public static DroneResponse toDroneResponse(Drone drone) {
        DroneResponse response = new DroneResponse();
        response.setDroneId(drone.getDroneId());
        response.setXCoordinate(drone.getCoordinateX());
        response.setYCoordinate(drone.getCoordinateY());
        response.setDirection(drone.getDirection());
        return response;
    }
}

