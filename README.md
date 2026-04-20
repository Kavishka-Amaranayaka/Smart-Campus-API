# Smart Campus Sensor & Room Management API

A fully RESTful JAX-RS API built with Jersey 2.39.1 on Apache Tomcat for managing campus rooms and IoT sensors as part of the Smart Campus initiative.

## API Design Overview

This API provides a robust, scalable interface for the University's Smart Campus initiative. It manages three core resources:

**Rooms** - Physical campus locations (libraries, labs, halls) with capacity management.
**Sensors** - IoT devices deployed inside rooms (Temperature, CO2, Occupancy).
**Sensor Readings** - Historical measurement logs per sensor with automatic parent value sync.

### Architecture Decisions

**JAX-RS (Jersey 2.39.1)** - Pure JAX-RS implementation, no Spring Boot.
**In-memory storage** - ConcurrentHashMap for thread-safe data management (no database).
**Singleton DataStore** - Shared application state across all request-scoped resource instances.
**Sub-Resource Locator Pattern** - Sensor readings delegated to dedicated SensorReadingResource class.
**Exception Mappers** - Custom mappers for 409, 422, 403, and 500 HTTP responses.
**JAX-RS Filter** - Centralized request/response logging via ContainerRequestFilter & ContainerResponseFilter.
**HATEOAS** - Discovery endpoint provides navigational links to all resources.

### Resource Hierarchy

/api/v1                              <- Discovery + HATEOAS links
/api/v1/rooms                        <- Room collection
/api/v1/rooms/{roomId}               <- Individual room
/api/v1/sensors                      <- Sensor collection (supports ?type= filter)
/api/v1/sensors/{sensorId}           <- Individual sensor
/api/v1/sensors/{sensorId}/readings  <- Sensor reading history (sub-resource)


### Error Handling Strategy

### HTTP Status                 ### Scenario 

409 Conflict                    Deleting a room that still has sensors assigned 
422 Unprocessable Entity        Registering a sensor with a non-existent roomId 
403 Forbidden                   Posting a reading to a MAINTENANCE/OFFLINE sensor 
500 Internal Server Error       Any unexpected runtime error (no stack trace exposed) 



## Build & Run Instructions

### Prerequisites

Java JDK 17 or higher
Apache Maven 3.6+
Apache Tomcat 9.x
Apache NetBeans IDE 25

### Step 1: Clone the Repository

1. git clone https://github.com/Kavishka-Amaranayaka/Smart-Campus-API.git
2. cd Smart-Campus-API


### Step 2: Open in NetBeans

1. Open **Apache NetBeans**
2. Click **File -> Open Project**
3. Navigate to the cloned "Smart-Campus-API" folder
4. Click **Open Project**

### Step 3: Configure Tomcat Server

1. Right-click the project -> **Properties**
2. Click **Run** category
3. Select your **Apache Tomcat 9.x** server
4. Set **Relative URL** to: "api/v1"
5. Click **OK**

### Step 4: Build the Project

1. Right-click project -> **Clean and Build**
2. Wait for "BUILD SUCCESS" in the Output window

### Step 5: Run the Project

1. Click the green **Run** button
2. Wait for Tomcat to deploy — Output window shows:
   OK - Started application at context path

### Step 6: Verify

Open your browser or Postman and navigate to:
http://localhost:8080/Smart_Campus_API/api/v1

You should see a JSON discovery response with API metadata and HATEOAS links.


## Endpoint Reference

### Discovery
### Method  Endpoint    Description 
GET         '/api/v1'   API metadata + HATEOAS navigation links 

### Room Management
### Method  Endpoint                  Description                             Success 
GET         '/api/v1/rooms'           List all rooms                          200 OK 
POST        '/api/v1/rooms'           Create new room                         201 Created
GET         '/api/v1/rooms/{roomId}'  Get room by ID                          200 OK
DELETE      '/api/v1/rooms/{roomId}'  Delete room (blocked if sensors exist)  200 OK / 409

### Sensor Operations
### Method  Endpoint                       Description              Success
GET         '/api/v1/sensors'              List all sensors         200 OK 
GET         '/api/v1/sensors?type=CO2`     Filter sensors by type   200 OK
POST        '/api/v1/sensors'              Register new sensor      201 Created / 422
GET         '/api/v1/sensors/{sensorId}'   Get sensor by ID         200 OK
DELETE      '/api/v1/sensors/{sensorId}'   Remove a sensor          200 OK

### Sensor Readings (Sub-Resource)
### Method  Endpoint                                Description               Success

GET         '/api/v1/sensors/{sensorId}/readings'   Get reading history       200 OK
POST        '/api/v1/sensors/{sensorId}/readings'   Add new reading           201 Created / 403

## Sample curl Commands

### 1. Discover the API
curl -X GET http://localhost:8080/Smart_Campus_API/api/v1 \
  -H "Accept: application/json"

### 2. Get All Rooms
curl -X GET http://localhost:8080/Smart_Campus_API/api/v1/rooms \
  -H "Accept: application/json"

### 3. Create a New Room
curl -X POST http://localhost:8080/Smart_Campus_API/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id": "SCI-205",
    "name": "Science Lab Upper Floor",
    "capacity": 40
  }'

### 4. Attempt to Delete a Room with Sensors (409 Conflict)
curl -X DELETE http://localhost:8080/Smart_Campus_API/api/v1/rooms/LIB-301 \
  -H "Accept: application/json"

### 5. Register a Sensor with Invalid roomId (422 Unprocessable Entity)
curl -X POST http://localhost:8080/Smart_Campus_API/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "TEMP-999",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "FAKE-ROOM-999"
  }'

### 6. Register a Valid Sensor
curl -X POST http://localhost:8080/Smart_Campus_API/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "CO2-005",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 400.0,
    "roomId": "SCI-205"
  }'

### 7. Filter Sensors by Type
curl -X GET "http://localhost:8080/Smart_Campus_API/api/v1/sensors?type=CO2" \
  -H "Accept: application/json"

### 8. Post a Reading to a MAINTENANCE Sensor (403 Forbidden)
curl -X POST http://localhost:8080/Smart_Campus_API/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 50}'

### 9. Post a Valid Sensor Reading
curl -X POST http://localhost:8080/Smart_Campus_API/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 25.5}'

### 10. Get Reading History for a Sensor
curl -X GET http://localhost:8080/Smart_Campus_API/api/v1/sensors/TEMP-001/readings \
  -H "Accept: application/json"


## Pre-loaded Test Data

### Type     ID            Details 
Room         'LIB-301'     Library, capacity 50 
Room         'LAB-101'     Computer Lab, capacity 30 
Room         'HALL-A'      Lecture Hall, capacity 200 
Sensor       'TEMP-001'    Temperature, **ACTIVE**, in LIB-301 
Sensor       'CO2-001'     CO2, **ACTIVE**, in LAB-101 
Sensor       'OCC-001'     Occupancy, **MAINTENANCE**, in HALL-A

-> 'OCC-001' is in MAINTENANCE - use it to test **403 Forbidden** when posting readings.
-> 'LIB-301' has 'TEMP-001' assigned - use it to test **409 Conflict** when deleting.
