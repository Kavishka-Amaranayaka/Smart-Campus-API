package com.smartcampus.model;

/**
 * Sensor entity - Represents a sensor deployed in a room.
 * status: "ACTIVE", "MAINTENANCE", "OFFLINE"
 */
public class Sensor {

    private String id;            // e.g. "TEMP-001"
    private String type;          // e.g. "Temperature", "CO2", "Occupancy"
    private String status;        // "ACTIVE", "MAINTENANCE", "OFFLINE"
    private double currentValue;  // Latest reading value
    private String roomId;        // Room this sensor belongs to

    // Default constructor
    public Sensor() {}

    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}
