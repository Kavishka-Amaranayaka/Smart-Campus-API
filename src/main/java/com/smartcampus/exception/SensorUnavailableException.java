package com.smartcampus.exception;

/**
 * PART 5.3 - Thrown when attempting to POST a reading to a sensor that is in MAINTENANCE or OFFLINE status.
 */
public class SensorUnavailableException extends RuntimeException {

    private final String sensorId;
    private final String status;

    public SensorUnavailableException(String sensorId, String status) {
        super("Sensor '" + sensorId + "' is currently in '" + status + "' state and cannot accept readings");
        this.sensorId = sensorId;
        this.status = status;
    }

    public String getSensorId() { return sensorId; }
    public String getStatus() { return status; }
}
