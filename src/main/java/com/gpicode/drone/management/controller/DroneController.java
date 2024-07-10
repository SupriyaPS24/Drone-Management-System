package com.gpicode.drone.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpicode.drone.management.dto.*;
import com.gpicode.drone.management.entity.Drone;
import com.gpicode.drone.management.entity.DroneHistory;
import com.gpicode.drone.management.exception.DroneHistoryNotFoundException;
import com.gpicode.drone.management.exception.DroneHistoryReadException;
import com.gpicode.drone.management.exception.DroneNotFoundException;
import com.gpicode.drone.management.exception.InvalidBoundaryException;
import com.gpicode.drone.management.service.DroneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/v1/api/drones")
public class DroneController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String storagePath = "drone-history/";

    @Autowired
    private DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
        initializeStoragePath();
    }

    private void initializeStoragePath() {
        this.storagePath = "drone-history/";
        createDirectoryIfNotExists(this.storagePath);
    }

    // Setter method to override storage path (for testing)
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        createDirectoryIfNotExists(this.storagePath);
    }

    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createDrone(@RequestBody @Valid RegisterDroneRequest request) throws InvalidBoundaryException {
        DroneResponse droneResponse = droneService.registerDrone(request);
        DroneHistory history = new DroneHistory();
        history.setDroneId(droneResponse.getDroneId());
        history.getStates().add(convertToMoveDroneResponse(droneResponse));

        saveDroneHistory(history);
        return new ResponseEntity<>(droneResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/move")
    public ResponseEntity<?> moveDrone(@PathVariable UUID id, @Valid @RequestBody MoveDroneRequest request) throws InvalidBoundaryException, DroneNotFoundException, DroneHistoryNotFoundException, DroneHistoryReadException {
        MoveDroneResponse droneResponse = droneService.moveDrone(id, request);
        DroneHistory history = loadDroneHistory(id);
        if (history == null) {
            throw new DroneNotFoundException("Drone not found");
        }

        history.getStates().add(droneResponse);
        saveDroneHistory(history);
        return new ResponseEntity<>(droneResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDrone(@PathVariable UUID id) throws DroneNotFoundException {
        Drone drone = droneService.getDrone(id);
        return new ResponseEntity<>(drone, HttpStatus.OK);
    }

    @GetMapping("/getDroneHistory/{id}")
    public ResponseEntity<?> getDroneHistory(@PathVariable UUID id) throws DroneHistoryReadException, DroneHistoryNotFoundException {
        DroneHistory history = loadDroneHistory(id);
        if (history == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Drone history not found");
        }

        return ResponseEntity.ok(history.getStates());
    }

    private void saveDroneHistory(DroneHistory history) {
        try {
            objectMapper.writeValue(new File(storagePath + history.getDroneId() + ".json"), history);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DroneHistory loadDroneHistory(UUID id) throws DroneHistoryNotFoundException, DroneHistoryReadException {
        File file = new File(storagePath + id + ".json");
        if (!file.exists()) {
            throw new DroneHistoryNotFoundException("Drone history not found for ID: " + id);
        }
        try {
            return objectMapper.readValue(file, DroneHistory.class);
        } catch (IOException exception) {
            throw new DroneHistoryReadException("Error reading drone history for ID: " + id);
        }
    }
    private MoveDroneResponse convertToMoveDroneResponse(DroneResponse droneResponse) {
        MoveDroneResponse moveDroneResponse = new MoveDroneResponse();
        moveDroneResponse.setDroneId(droneResponse.getDroneId());
        moveDroneResponse.setXCoordinate(droneResponse.getXCoordinate());
        moveDroneResponse.setYCoordinate(droneResponse.getYCoordinate());
        moveDroneResponse.setDirection(droneResponse.getDirection());
        return moveDroneResponse;
    }
}
