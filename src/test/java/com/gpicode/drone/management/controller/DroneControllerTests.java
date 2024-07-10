package com.gpicode.drone.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpicode.drone.management.dto.*;
import com.gpicode.drone.management.entity.Drone;
import com.gpicode.drone.management.exception.*;
import com.gpicode.drone.management.service.DroneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DroneControllerTests {

    @Mock
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir; // JUnit will provide a temporary directory for each test

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DroneService droneService;

    @InjectMocks
    private DroneController droneController;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();

        // Set storage path to test resources directory for testing
        String testResourcePath = Paths.get("src", "test", "resources", "test-drone-history").toString() + File.separator;
        droneController.setStoragePath(testResourcePath);
    }

    @Test
    public void testCreateDrone_Success() throws InvalidBoundaryException {
        RegisterDroneRequest request = new RegisterDroneRequest(5, 5, "North");
        DroneResponse expectedResponse = new DroneResponse(UUID.randomUUID(), 5, 5, "North");
        when(droneService.registerDrone(request)).thenReturn(expectedResponse);

        ResponseEntity<?> responseEntity = droneController.createDrone(request);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isInstanceOf(DroneResponse.class);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);

        verify(droneService, times(1)).registerDrone(request);
    }

    @Test
    public void testMoveDrone_Success() throws InvalidBoundaryException, DroneNotFoundException, DroneHistoryReadException, DroneHistoryNotFoundException {
        UUID droneId = UUID.fromString("b9cbc74f-0e2e-4b53-9659-f0a34a6c69be");
        MoveDroneRequest request = new MoveDroneRequest(7, 7);
        List<String> details = Arrays.asList("Pointed from East to North", "Moved to (7,7)");
        MoveDroneResponse expectedResponse = new MoveDroneResponse(droneId, 7, 7, "North", details);
        when(droneService.moveDrone(droneId, request)).thenReturn(expectedResponse);

        ResponseEntity<?> responseEntity = droneController.moveDrone(droneId, request);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isInstanceOf(MoveDroneResponse.class);

        MoveDroneResponse actualResponse = (MoveDroneResponse) responseEntity.getBody();
        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(droneService, times(1)).moveDrone(droneId, request);
    }


    @Test
    public void testGetDrone_Success() throws DroneNotFoundException {
        UUID droneId = UUID.randomUUID();
        Drone expectedDrone = new Drone(droneId, 5, 5, "North");
        when(droneService.getDrone(droneId)).thenReturn(expectedDrone);
        ResponseEntity<?> responseEntity = droneController.getDrone(droneId);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isInstanceOf(Drone.class);
        assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(expectedDrone);
        verify(droneService, times(1)).getDrone(droneId);
    }

    @Test
    public void testGetDrone_DroneNotFound() throws DroneNotFoundException {
        UUID nonExistingDroneId = UUID.randomUUID();
        when(droneService.getDrone(nonExistingDroneId)).thenThrow(new DroneNotFoundException("Drone not found"));
        try {
            droneController.getDrone(nonExistingDroneId);
        } catch (DroneNotFoundException ex) {
            ResponseEntity<ErrorResponse> responseEntity =
                    globalExceptionHandler.handleDroneNotFoundException(ex);
            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseEntity.getBody()).isNotNull();
            assertThat(responseEntity.getBody().getMessage()).isEqualTo("Invalid droneId");
            verify(droneService, times(1)).getDrone(nonExistingDroneId);
        }
    }

    @Test
    public void testCreateDrone_InvalidBoundaryException() throws InvalidBoundaryException {
        RegisterDroneRequest request = new RegisterDroneRequest(5, 5, "North");
        when(droneService.registerDrone(request)).thenThrow(new InvalidBoundaryException("Invalid boundary"));

        try {
            droneController.createDrone(request);
        } catch (InvalidBoundaryException ex) {
            ResponseEntity<ErrorResponse> responseEntity =
                    globalExceptionHandler.handleInvalidBoundaryException(ex);
            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseEntity.getBody()).isNotNull();
            assertThat(responseEntity.getBody().getMessage()).isEqualTo("Invalid values");
            assertThat(responseEntity.getBody().getDetails().get(0)).isEqualTo("Invalid boundary");
            verify(droneService, times(1)).registerDrone(request);
        }
    }

    @Test
    public void testMoveDrone_IllegalArgumentException() throws InvalidBoundaryException, DroneNotFoundException {
        UUID droneId = UUID.randomUUID();
        MoveDroneRequest request = new MoveDroneRequest(7, 7);
        when(droneService.moveDrone(droneId, request)).thenThrow(new IllegalArgumentException("Invalid move"));

        try {
            droneController.moveDrone(droneId, request);
        } catch (IllegalArgumentException | DroneHistoryNotFoundException | DroneHistoryReadException ex) {
            ResponseEntity<ErrorResponse> responseEntity =
                    globalExceptionHandler.handleIllegalArgumentException((IllegalArgumentException) ex);
            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseEntity.getBody()).isNotNull();
            assertThat(responseEntity.getBody().getMessage()).isEqualTo("Invalid Input");
            assertThat(responseEntity.getBody().getDetails().get(0)).isEqualTo("Invalid move");
            verify(droneService, times(1)).moveDrone(droneId, request);
        }
    }

    @Test
    public void testCreateDrone_ValidationException() {
        RegisterDroneRequest invalidRequest = new RegisterDroneRequest(-1, -1, "North");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("registerDroneRequest", "x", "must be greater than or equal to 0"),
                new FieldError("registerDroneRequest", "y", "must be greater than or equal to 0")
        ));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleValidationExceptions(ex);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Validation Failed");
        assertThat(responseEntity.getBody().getDetails().size()).isEqualTo(2);
        assertThat(responseEntity.getBody().getDetails().get(0)).isEqualTo("x: must be greater than or equal to 0");
        assertThat(responseEntity.getBody().getDetails().get(1)).isEqualTo("y: must be greater than or equal to 0");
    }

}
