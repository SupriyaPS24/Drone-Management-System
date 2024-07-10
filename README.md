# Drone Management System

## Overview

The Drone Management System is a Spring Boot application designed to simulate the management of drone within a farm field. \ 
This implementation focus on Registering the drone, managing the drone movements and retrieving the position and direction of drone on the field. field is represented as a 10x10 square meter area.

## Features of the application
1. **Register the drone**: User can register a new drone with the specified initial position with (X, Y) coordinates and direction.
2. **Move the drone**: User can move the required drone to a new position with (X, Y) coordinates within the field boundaries.
3. **Get drone details**: User can get the details of the position and direction of the particular drone, by providing the appropriate drone-Id.

## Architectural decisions made during designing the application:

1. `Register a new drone:` Registration of new drone should not be allowed, if another drone previously registered is already <u>in that position.</u>
2. `Move the drone:` <u>Drone's direction</u> is not provided in the requestBody for move drone, instead it should be <u>determined my the application.</u>
3.  Directions like North-East, South-West, South-East, North-West is not considered, also drone cannot move diagonally.

## Application Setup

1. **Clone the Repository**
   ```sh
   git clone https://github.com/SupriyaPS24/Drone-Management-System.git
   cd Drone-Management-System
### Below are the two ways to build and run the application.

1. ### Through terminal
* **Build Project**
    ```sh
   mvn clean package

* **Run Application**
    ```sh
   mvn spring-boot:run 
   
2. ### Through Shell script
The application contains script file named build.sh with commands to build and run the application.

* **make script file executable, using below command** 
 ```sh
   chmod +x build.sh
 ```
* **run the script** 
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

## Functionality overview

1. **Registering a new drone**<br>
Validates field boundaries, checks if any drone is present in the provided coordinates, and ensures the drone is registered correctly.

**Http method:** POST  
**Endpoint:** `v1/api/drones/register`  
**Example JSON Request Body:**
```json
{
  "xCoordinate": 4,
  "yCoordinate": 2,
  "direction": "South"
}
```
\
**Example curl command**
```sh
curl --location 'http://localhost:8080/v1/api/drones/register' \
--header 'Content-Type: application/json' \
--data '{
    "xCoordinate": 4,
    "yCoordinate": 2,
    "direction": "South"
}'
```
**Response:** \
`201 - Created, with response body` \
`400/405 - Bad Request and Method Not Found - Failure, with error details`

2. **Moving a drone within field**: \
If the drone with provided droneId is registered previously, and coordinates are within field boundaries and no drone is present in the given coordinates, 
move the drone to provided coordinates giving the details of movement. \
Drone cannot directly point or move in 180 degree direction (ie., from South to North or East to West and vice versa)

**Http method:** PUT \
**Endpoint:** v1/api/drones/{droneId}/move \
**Example JSON Request Body:**
```json
{
"xCoordinate": 4,
"yCoordinate":2
}
``` 
 \
**Example curl command**
```sh
curl --location --request PUT 'http://localhost:8080/v1/api/drones/e5d9cf86-065f-4df9-b969-09cbd4a4ff20/move' \
--header 'Content-Type: application/json' \
--data '{
    "xCoordinate": 4,
    "yCoordinate": 2
}'
```
**Response:** \
`200 - OK, with response body`\
`404/400/405 - Drone Not Found or Bad Request, with error details`

3. **Get Drone details**: \
If the drone with provided droneId is registered, provide the coordinate and direction details of the drone.<br>

**Http method:** GET \
**Endpoint:** v1/api/drones/{droneId} \
\
**Example curl command**
```sh
curl --location 'http://localhost:8080/drones/7ed8815f-ea48-4f23-89d0-380d956e2d38'
```
**Response:** \
`200 - OK, with response body`\
`404 - Drone not found or Bad Request, with error details`

4. **Get Drone History**: \
This endpoint helps us to keep track of the drone movements, as DroneHistory in json file.<br>
If the drone with given droneId is registered, provide the movement details of the drone in response. 

**Http method:** GET \
**Endpoint:** v1/api/drones/getDroneHistory/{droneId} \
\
**Example curl command**
```sh
curl --location 'http://localhost:8080/v1/api/drones/getDroneHistory/e5d9cf86-065f-4df9-b969-09cbda4ff20'
```
**Response:** \
`200 - OK, with response body`\
`404 - Drone not found or file not found message`

5. **Exception Handling**:<br> Comprehensive global exception handling for better error management, providing specific exception types with detailed error responses.<br>
6. **Unit Tests**:<br> Ensures core functionalities such as Registering drone, moving and retrieving drone details are thoroughly tested using JUnit and Mockito.<br>

## Future Enhancements
1. **Additional APIs/Endpoints**: 

* *Examples:* \
`- Deregister the drone` User should be able to deregister the registered drone by providing the drone Id or in other case, 
the application should be able to deregister the drone, when new drone is trying to register to the same coordinates.<br> \
`- change direction of the drone` User should be provided with option to just change the direction of the drone without changing coordinates.<br>
2. **Detailed Logging**: Add more detailed logging for better traceability and debugging.<br>
3. **API Documentation**: Use tools like Swagger to provide comprehensive API documentation for easier integration and usage.<br>
