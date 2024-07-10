package com.gpicode.drone.management.repository;

import com.gpicode.drone.management.entity.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DroneRepository extends JpaRepository<Drone, UUID> {
    boolean existsByCoordinateXAndCoordinateY(Integer xCoordinate, Integer yCoordinate);
}
