package com.gpicode.drone.management.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DroneDTOTest {

    @Test
    public void testRegisterDroneRequest() {
        RegisterDroneRequest request = new RegisterDroneRequest();
        request.setXCoordinate(5);
        request.setYCoordinate(5);
        request.setDirection("NORTH");

        assertEquals(5, request.getXCoordinate());
        assertEquals(5, request.getYCoordinate());
        assertEquals("NORTH", request.getDirection());
    }

    @Test
    public void testMoveDroneRequest() {
        MoveDroneRequest request = new MoveDroneRequest();
        request.setXCoordinate(7);
        request.setYCoordinate(7);

        assertEquals(7, request.getXCoordinate());
        assertEquals(7, request.getYCoordinate());
    }

    @Test
    public void testDroneResponse() {
        UUID droneId = UUID.randomUUID();
        DroneResponse response = new DroneResponse(droneId, 2, 3, "EAST");
        assertEquals(droneId, response.getDroneId());
        assertEquals(2, response.getXCoordinate());
        assertEquals(3, response.getYCoordinate());
        assertEquals("EAST", response.getDirection());
    }

    @Test
    public void testMoveDroneResponse() {
        UUID droneId = UUID.randomUUID();
        List<String> details = new ArrayList<>();
        details.add("Point from North to East");
        details.add("move to (2,3)");
        MoveDroneResponse response = new MoveDroneResponse(droneId, 2, 3, "EAST",details);

        assertEquals(droneId, response.getDroneId());
        assertEquals(2, response.getXCoordinate());
        assertEquals(3, response.getYCoordinate());
        assertEquals("EAST", response.getDirection());
        assertEquals(details,response.getDetails());
    }
}
