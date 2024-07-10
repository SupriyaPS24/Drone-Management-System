package com.gpicode.drone.management.service;

import com.gpicode.drone.management.dto.DroneResponse;
import com.gpicode.drone.management.dto.MoveDroneRequest;
import com.gpicode.drone.management.dto.MoveDroneResponse;
import com.gpicode.drone.management.dto.RegisterDroneRequest;
import com.gpicode.drone.management.entity.Drone;
import com.gpicode.drone.management.exception.DroneNotFoundException;
import com.gpicode.drone.management.exception.InvalidBoundaryException;
import com.gpicode.drone.management.mapper.DroneMapper;
import com.gpicode.drone.management.repository.DroneRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
    public class DroneService {
        @Autowired
        private DroneRepository droneRepository;

        public DroneService(DroneRepository droneRepository) {
            this.droneRepository = droneRepository;
        }

        public enum Direction {
            NORTH, EAST, SOUTH, WEST;
        }

        @Transactional
        public DroneResponse registerDrone(RegisterDroneRequest droneDetails) throws InvalidBoundaryException {
            validatePosition(droneDetails.getXCoordinate(), droneDetails.getYCoordinate());

            if (droneRepository.existsByCoordinateXAndCoordinateY(droneDetails.getXCoordinate(), droneDetails.getYCoordinate())) {
                throw new IllegalArgumentException("Another drone already exists in this position");
            }
            Drone drone = DroneMapper.toDroneEntity(droneDetails);
            Drone registeredDrone = droneRepository.save(drone);
            return DroneMapper.toDroneResponse(registeredDrone);
        }

    public MoveDroneResponse moveDrone(UUID id, MoveDroneRequest request) throws InvalidBoundaryException, DroneNotFoundException {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found"));
        validatePosition(request.getXCoordinate(), request.getYCoordinate());

        if (drone.getCoordinateX() == request.getXCoordinate() && drone.getCoordinateY() == request.getYCoordinate()) {
            throw new IllegalArgumentException("Drone is already at the specified position, no movement detected");
        }

        if (droneRepository.existsByCoordinateXAndCoordinateY(request.getXCoordinate(), request.getYCoordinate())) {
            throw new IllegalArgumentException("Another drone already exists at the new position");
        }

        List<String> details = new ArrayList<>();
        validateMove(drone, request.getXCoordinate(), request.getYCoordinate(), details);

        DroneMapper.updateDroneEntity(drone, request);
        Drone updatedDrone = droneRepository.save(drone);

        return new MoveDroneResponse(updatedDrone.getDroneId(), updatedDrone.getCoordinateX(), updatedDrone.getCoordinateY(), updatedDrone.getDirection(), details);
    }

    private void validatePosition(int x, int y) throws InvalidBoundaryException {
        if (x < 0 || x > 9 || y < 0 || y > 9) {
            throw new InvalidBoundaryException("Out of field values for Coordinates");
        }
    }

    private void validateMove(Drone drone, int x, int y, List<String> details) {
        Direction currentDirection = Direction.valueOf(drone.getDirection().toUpperCase());
        int currentX = drone.getCoordinateX();
        int currentY = drone.getCoordinateY();

        if (currentX != x && currentY != y) {
            handleTwoAxisChange(drone, x, y, details);
        } else {
            // Handle X-axis movement first
            if (currentX != x) {
                if (currentX < x) {
                    changeDirectionIfNeeded(drone, Direction.EAST, details);
                } else {
                    changeDirectionIfNeeded(drone, Direction.WEST, details);
                }
                details.add("Moved to (" + x + ", " + currentY + ")");
            }
            // Handle Y-axis movement next
            if (currentY != y) {
                if (currentY < y) {
                    changeDirectionIfNeeded(drone, Direction.NORTH, details);
                } else {
                    changeDirectionIfNeeded(drone, Direction.SOUTH, details);
                }
                details.add("Moved to (" + x + ", " + y + ")");
            }
        }
    }

    private void handleTwoAxisChange(Drone drone, int x, int y, List<String> details) {
        int currentX = drone.getCoordinateX();
        int currentY = drone.getCoordinateY();
        Direction currentDirection = Direction.valueOf(drone.getDirection().toUpperCase());

        // Determine intermediate directions
        Direction xDirection = currentX < x ? Direction.EAST : Direction.WEST;
        Direction yDirection = currentY < y ? Direction.NORTH : Direction.SOUTH;

        // Move along the axis that doesn't require a 180-degree turn first
        if (currentDirection == Direction.NORTH || currentDirection == Direction.SOUTH) {
            // Move along X-axis first
            if (currentX != x) {
                if (currentDirection == Direction.NORTH && xDirection == Direction.SOUTH ||
                        currentDirection == Direction.SOUTH && xDirection == Direction.NORTH) {
                    Direction intermediateDirection = Direction.EAST;
                    details.add("Pointed from " + currentDirection + " to " + intermediateDirection);
                    drone.setDirection(intermediateDirection.name());
                    currentDirection = intermediateDirection;
                }
                if (currentDirection != xDirection) {
                    details.add("Pointed from " + currentDirection + " to " + xDirection);
                    drone.setDirection(xDirection.name());
                }
                currentDirection = xDirection;
                drone.setCoordinateX(x);
                details.add("Moved to (" + x + ", " + currentY + ") towards " + xDirection);
            }

            // Then move along Y-axis
            if (currentY != y) {
                if (currentDirection != yDirection) {
                    details.add("Pointed from " + currentDirection + " to " + yDirection);
                    drone.setDirection(yDirection.name());
                }
                drone.setCoordinateY(y);
                details.add("Moved to (" + x + ", " + y + ") towards " + yDirection);
            }
        } else {
            // Move along Y-axis first
            if (currentY != y) {
                if (currentDirection == Direction.EAST && yDirection == Direction.WEST ||
                        currentDirection == Direction.WEST && yDirection == Direction.EAST) {
                    Direction intermediateDirection = Direction.NORTH;
                    details.add("Pointed from " + currentDirection + " to " + intermediateDirection);
                    drone.setDirection(intermediateDirection.name());
                    currentDirection = intermediateDirection;
                }
                if (currentDirection != yDirection) {
                    details.add("Pointed from " + currentDirection + " to " + yDirection);
                    drone.setDirection(yDirection.name());
                }
                currentDirection = yDirection;
                drone.setCoordinateY(y);
                details.add("Moved to (" + currentX + ", " + y + ") towards " + yDirection);
            }

            // Then move along X-axis
            if (currentX != x) {
                if (currentDirection != xDirection) {
                    details.add("Pointed from " + currentDirection + " to " + xDirection);
                    drone.setDirection(xDirection.name());
                }
                drone.setCoordinateX(x);
                details.add("Moved to (" + x + ", " + y + ") towards " + xDirection);
            }
        }
    }

    private void changeDirectionIfNeeded(Drone drone, Direction newDirection, List<String> details) {
        Direction currentDirection = Direction.valueOf(drone.getDirection().toUpperCase());

        // Check for 180-degree turn and change direction accordingly
        if ((currentDirection == Direction.NORTH && newDirection == Direction.SOUTH) ||
                (currentDirection == Direction.SOUTH && newDirection == Direction.NORTH) ||
                (currentDirection == Direction.EAST && newDirection == Direction.WEST) ||
                (currentDirection == Direction.WEST && newDirection == Direction.EAST)) {
            Direction intermediateDirection = (newDirection == Direction.NORTH || newDirection == Direction.SOUTH) ? Direction.EAST : Direction.NORTH;
            details.add("Could not directly travel from " + currentDirection + " to " + newDirection + ", pointed towards " + intermediateDirection + " first.");
            drone.setDirection(intermediateDirection.name());
            currentDirection = intermediateDirection;
        }
        details.add("Pointed from " + currentDirection + " to " + newDirection);
        drone.setDirection(newDirection.name());
    }

    public Drone getDrone(UUID id) throws DroneNotFoundException {
            return droneRepository.findById(id).orElseThrow(() -> new DroneNotFoundException("Drone not found"));
        }
    }

