package com.gpicode.drone.management.service;

import com.gpicode.drone.management.controller.DroneController;
import com.gpicode.drone.management.dto.DroneResponse;
import com.gpicode.drone.management.dto.MoveDroneRequest;
import com.gpicode.drone.management.dto.MoveDroneResponse;
import com.gpicode.drone.management.dto.RegisterDroneRequest;
import com.gpicode.drone.management.entity.Drone;
import com.gpicode.drone.management.exception.DroneNotFoundException;
import com.gpicode.drone.management.exception.InvalidBoundaryException;
import com.gpicode.drone.management.mapper.DroneMapper;
import com.gpicode.drone.management.repository.DroneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DroneServiceTests {

    @Mock
    private DroneController droneController;
    @Mock
    private DroneRepository droneRepository;

    @InjectMocks
    private DroneService droneService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterDrone_Success() throws InvalidBoundaryException {
        RegisterDroneRequest request = new RegisterDroneRequest(5, 5, "North".toUpperCase());
        Drone droneEntity = DroneMapper.toDroneEntity(request);
        when(droneRepository.existsByCoordinateXAndCoordinateY(request.getXCoordinate(), request.getYCoordinate()))
                .thenReturn(false);
        when(droneRepository.save(any(Drone.class))).thenReturn(droneEntity);
        DroneResponse response = droneService.registerDrone(request);
        assertNotNull(response);
        assertEquals(request.getXCoordinate(), response.getXCoordinate());
        assertEquals(request.getYCoordinate(), response.getYCoordinate());
        assertEquals(request.getDirection(), response.getDirection());

        verify(droneRepository, times(1)).existsByCoordinateXAndCoordinateY(request.getXCoordinate(), request.getYCoordinate());
        verify(droneRepository, times(1)).save(any(Drone.class));
    }

    @Test
    public void testRegisterDrone_DroneAlreadyExists() {
        RegisterDroneRequest request = new RegisterDroneRequest(5, 5, "North");

        when(droneRepository.existsByCoordinateXAndCoordinateY(request.getXCoordinate(), request.getYCoordinate()))
                .thenReturn(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> droneService.registerDrone(request));
        assertEquals("Another drone already exists in this position", exception.getMessage());
        verify(droneRepository, times(1)).existsByCoordinateXAndCoordinateY(request.getXCoordinate(), request.getYCoordinate());
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    public void testMoveDrone_Success() throws InvalidBoundaryException, DroneNotFoundException {
        UUID droneId = UUID.randomUUID();
        MoveDroneRequest request = new MoveDroneRequest(7, 7);

        Drone existingDrone = new Drone(droneId, 5, 5, "North");
        Drone updatedDrone = new Drone(droneId, request.getXCoordinate(), request.getYCoordinate(), "North");

        when(droneRepository.findById(droneId)).thenReturn(java.util.Optional.of(existingDrone));
        when(droneRepository.existsByCoordinateXAndCoordinateY(request.getXCoordinate(), request.getYCoordinate()))
                .thenReturn(false);
        when(droneRepository.save(any(Drone.class))).thenReturn(updatedDrone);
        MoveDroneResponse response = droneService.moveDrone(droneId, request);
        assertNotNull(response);
        assertEquals(request.getXCoordinate(), response.getXCoordinate());
        assertEquals(request.getYCoordinate(), response.getYCoordinate());

        verify(droneRepository, times(1)).findById(droneId);
        verify(droneRepository, times(1)).existsByCoordinateXAndCoordinateY(request.getXCoordinate(), request.getYCoordinate());
        verify(droneRepository, times(1)).save(any(Drone.class));
    }

    @Test
    public void testMoveDrone_SouthDirection() throws InvalidBoundaryException, DroneNotFoundException {
        testMoveDrone(5, 5, "South", 5, 7, false, false);
    }

    @Test
    public void testMoveDrone_NorthDirection() throws InvalidBoundaryException, DroneNotFoundException {
        testMoveDrone(5, 5, "North", 7, 7, false, false);
    }

    @Test
    public void testMoveDrone_EastDirection() throws InvalidBoundaryException, DroneNotFoundException {
        testMoveDrone(5, 5, "East", 7, 7, false, false);
    }

    @Test
    public void testMoveDrone_WestDirection() throws InvalidBoundaryException, DroneNotFoundException {
        testMoveDrone(5, 5, "East", 7, 5, false, false);
    }

    @Test
    public void testMoveDrone_DroneNotFound() {
        UUID droneId = UUID.randomUUID();
        MoveDroneRequest request = new MoveDroneRequest(7, 7);
        when(droneRepository.findById(droneId)).thenReturn(java.util.Optional.empty());
        assertThrows(DroneNotFoundException.class, () -> droneService.moveDrone(droneId, request));

        verify(droneRepository, times(1)).findById(droneId);
        verifyNoMoreInteractions(droneRepository);
    }

    @Test
    public void testMoveDrone_DroneAtSamePosition() throws InvalidBoundaryException, DroneNotFoundException {
        testMoveDrone(5, 5, "North", 5, 5, true, false);
    }

    @Test
    public void testMoveDrone_DroneAtNewPositionOccupied() throws InvalidBoundaryException, DroneNotFoundException {
        testMoveDrone(5, 5, "North", 7, 7, false, true);
    }

    @Test
    public void testMoveDrone_InvalidMoveDirection() throws InvalidBoundaryException, DroneNotFoundException {
        testMoveDrone(5, 5, "North", 5, 6, false, false);
    }



    private void testMoveDrone(int initialX, int initialY, String initialDirection,
                               int newX, int newY, boolean expectSamePositionError,
                               boolean expectOccupiedError) throws InvalidBoundaryException, DroneNotFoundException {
        UUID droneId = UUID.randomUUID();
        MoveDroneRequest request = new MoveDroneRequest(newX, newY);

        Drone existingDrone = new Drone(droneId, initialX, initialY, initialDirection);
        when(droneRepository.findById(droneId)).thenReturn(java.util.Optional.of(existingDrone));

        if (expectSamePositionError) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> droneService.moveDrone(droneId, request));
            assertEquals("Drone is already at the specified position, no movement detected", exception.getMessage());
        } else if (expectOccupiedError) {
            when(droneRepository.existsByCoordinateXAndCoordinateY(newX, newY)).thenReturn(true);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> droneService.moveDrone(droneId, request));
            assertEquals("Another drone already exists at the new position", exception.getMessage());
            verify(droneRepository, times(1)).existsByCoordinateXAndCoordinateY(newX, newY);
        } else {
            when(droneRepository.existsByCoordinateXAndCoordinateY(newX, newY)).thenReturn(false);
            Drone updatedDrone = new Drone(droneId, newX, newY, initialDirection);
            when(droneRepository.save(any(Drone.class))).thenReturn(updatedDrone);
            MoveDroneResponse response = droneService.moveDrone(droneId, request);
            assertNotNull(response);
            assertEquals(newX, response.getXCoordinate());
            assertEquals(newY, response.getYCoordinate());
            verify(droneRepository, times(1)).save(any(Drone.class));
        }
        verify(droneRepository, times(1)).findById(droneId);
    }

    @Test
    public void testGetDrone_Success() throws DroneNotFoundException {
        // Mock data
        UUID droneId = UUID.randomUUID();
        Drone drone = new Drone(droneId, 5, 5, "North");
        Optional<Drone> optionalDrone = Optional.of(drone);
        when(droneRepository.findById(droneId)).thenReturn(optionalDrone);
        Drone retrievedDrone = null;
        try {
            retrievedDrone = droneService.getDrone(droneId);
        } catch (DroneNotFoundException e) {
            e.printStackTrace();
        }
        assertNotNull(retrievedDrone);
        assertEquals(drone.getDroneId(), retrievedDrone.getDroneId());
        assertEquals(drone.getCoordinateX(), retrievedDrone.getCoordinateX());
        assertEquals(drone.getCoordinateY(), retrievedDrone.getCoordinateY());
        assertEquals(drone.getDirection(), retrievedDrone.getDirection());
        verify(droneRepository, times(1)).findById(droneId);
    }

    @Test
    public void testGetDrone_DroneNotFound() {
        UUID nonExistingDroneId = UUID.randomUUID();
        when(droneRepository.findById(nonExistingDroneId)).thenReturn(Optional.empty());
        DroneNotFoundException exception = assertThrows(DroneNotFoundException.class, () -> {
            droneService.getDrone(nonExistingDroneId);
        });
        assertEquals("Drone not found", exception.getMessage());
        verify(droneRepository, times(1)).findById(nonExistingDroneId);
    }

}
