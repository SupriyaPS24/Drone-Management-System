# Drone Management System

## Overview

The Drone Management System is a Spring Boot application designed to simulate the management of a drone within a farm field.<br> 
This implementation focus on Registering the drone, managing the drone movements and retrieving the position and direction of drone on the field. field is represented as a 10x10 square meter area.

## Features of the application
1. **Register the drone**: User can register a new drone with the specified initial position with (X, Y) coordinates and direction.
2. **Move the drone**: User can move the required drone to a new position with (X, Y) coordinates within the field boundaries.
3. **Get drone details**: User can get the details of the position and direction of the particular drone, by providing the appropriate droneId.

## Architectural decisions made during designing the application:

1. `Register a new drone:` Registration of new drone should not be allowed, if another drone previously registered is already in that position.<br>
2. `Move the drone:` Drone's direction is not provided in the requestBody for move drone, instead it should be determined my the application.
3.  Directions like North-East, South-West, South-East, North-West is not considered, also drone cannot move diagonally.

## How do I run it

1. **Clone the Repository**
   ```sh
   git clone <repository-url>
   cd <repository-directory>
### Below are the steps to build and run the application.

### Through terminal
1. **Build Project**
    ```sh
   mvn clean package
2. **Run Application**
    ```sh
   mvn spring-boot:run 
   
### Through Shell script
The application contains script file named build.sh with commands to build and run the application.

**Steps to make script file executable** 
 ```sh
   chmod +x build.sh
 ```
**run the script** 
 ```sh
   ./build.sh
 ```

## Endpoints
- **Register Drone**: `POST v1/api/drones/register`
- **Move Drone**: `PUT v1/api/drones/{droneId}/move`
- **Get Drone**: `GET v1/api/drones/{droneId}`
### Additional Endpoints
- **Get Drone History** `GET v1/api/drone/getDroneHistory/{droneId}`
- **Swagger Api Documentation**: `GET /swagger-ui.html`
- **Swagger Api Json** `GET /v3/api-docs`

## What are covered

1. **Registering a new drone**<br>
Validates field boundaries, checks if any drone is present in the provided coordinates, and ensures the drone is registered correctly.

**Http method:** POST  
**Endpoint:** `v1/api/drones/register`  
**JSON Request Body:**
```json
{
  "xCoordinate": 4,
  "yCoordinate": 2,
  "direction": "south"
}
```
\
**curl command for /register endpoint**
```sh
curl --location 'http://localhost:8080/v1/api/drones/register' \
--header 'Content-Type: application/json' \
--data '{
    "xCoordinate": 8,
    "yCoordinate": 7,
    "direction": "north"
}'
```
**Response:** \
`201 - Created, with response body` \
`400/405 - badRequest and method not found - failure, with error details`

2. **Moving a drone within field**: \
If the drone with provided droneId is registered, and coordinates are within field boundaries and no drone is present in the provided coordinates, 
move the drone to provided coordinates giving the details of movement. \
Drone cannot directly point or move in 180 degree direction (ie., from South to North or East to West and vice versa)

**Http method:** PUT \
**Endpoint:** v1/api/drones/{droneId}/move \
**JSON Request Body:**
```json
{
"xCoordinate": 4,
"yCoordinate":2
}
``` 
 \
**curl command for /moveDrone endpoint**
```sh
curl --location --request PUT 'http://localhost:8080/v1/api/drones/e5d9cf86-065f-4df9-b969-09cbd4a4ff20/move' \
--header 'Content-Type: application/json' \
--data '{
    "xCoordinate": 3,
    "yCoordinate": 7
}'
```
**Response:** \
`200 - OK, with response body`\
`404/400/405 - Drone not found or Bad Requests, with error details`

3. **Get Drone details**: \
If the drone with provided droneId is registered, provide the coordinate and direction details of the drone.<br>

**Http method:** GET \
**Endpoint:** v1/api/drones/{droneId} \
\
**curl command for getDrone details**
```sh
curl --location 'http://localhost:8080/drones/7ed8815f-ea48-4f23-89d0-380d956e2d38'
```
**Response:** \
`200 - OK, with response body`\
`404 - Drone not found or Bad Requests, with error details`

4. **Get Drone History**: \
This endpoint help us to keep track of the drone movements, as DroneHistory.
If the drone with provided droneId is registered, provide the registration and movement details of the drone in response. \

**Http method:** GET \
**Endpoint:** v1/api/drones/getDroneHistory/{droneId} \
\
**curl command for /getDroneHistory endpoint**
```sh
curl --location 'http://localhost:8080/v1/api/drones/getDroneHistory/e5d9cf86-065f-4df9-b969-09cbda4ff20'
```
**Response:** \
`200 - OK, with response body`\
`404 - Drone not found or file not found message`

5. **Exception Handling**:<br> Comprehensive global exception handling for better error management, providing specific exception types with detailed error responses.<br>
6. **Unit Tests**:<br> Ensures core functionalities such as Registering drone, moving and retrieving drone details are thoroughly tested using JUnit and Mockito.<br>

## How can we improve it in the future
1. **Additional APIs/Endpoints**: 

*Examples:* \
`Deregister the drone` User should be able to deregister the registered drone by providing the drone Id or in other case, 
Application should be able to deregister the drone, when new drone is trying to register to same coordinates.<br>

`change direction of the drone` User should be provided with option to just change the direction of the drone without changing coordinates.<br>
2. **Detailed Logging**: Add more detailed logging for better traceability and debugging.<br>
3. **API Documentation**: Use tools like Swagger to provide comprehensive API documentation for easier integration and usage.<br>
4. **Add More Tests**: Add more tests to cover all cases for the service layers.