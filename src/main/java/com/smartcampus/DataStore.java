package com.smartcampus;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DataStore - Central in-memory data store for the application.
 *
 * Uses the Singleton pattern - a single instance throughout the application lifetime.
 * Uses ConcurrentHashMap to ensure thread safety.
 * (Since JAX-RS has a request-scoped lifecycle, multiple threads may access data simultaneously)
 */
public class DataStore {

    // Singleton instance
    private static final DataStore INSTANCE = new DataStore();

    // Thread-safe in-memory collections
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    // Private constructor - singleton
    private DataStore() {
        seedData();
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    // --- Room Methods ---
    public Map<String, Room> getRooms() { return rooms; }

    public Room getRoomById(String id) { return rooms.get(id); }

    public void addRoom(Room room) { rooms.put(room.getId(), room); }

    public boolean deleteRoom(String id) {
        if (!rooms.containsKey(id)) return false;
        rooms.remove(id);
        return true;
    }

    // --- Sensor Methods ---
    public Map<String, Sensor> getSensors() { return sensors; }

    public Sensor getSensorById(String id) { return sensors.get(id); }

    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        sensorReadings.put(sensor.getId(), new ArrayList<>());
    }

    // --- SensorReading Methods ---
    public List<SensorReading> getReadingsForSensor(String sensorId) {
        return sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }

    public void addReading(String sensorId, SensorReading reading) {
        sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }

    /**
     * Sample data set - Pre-loaded when the application starts.
     */
    private void seedData() {
        // Rooms
        Room r1 = new Room("LIB-301", "Library", 50);
        Room r2 = new Room("LAB-101", "Computer Lab", 30);
        Room r3 = new Room("HALL-A", "Lecture Hall", 200);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
        rooms.put(r3.getId(), r3);

        // Sensors
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor s2 = new Sensor("CO2-001", "CO2", "ACTIVE", 412.0, "LAB-101");
        Sensor s3 = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "HALL-A");

        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);

        // Link sensors to rooms
        r1.getSensorIds().add(s1.getId());
        r2.getSensorIds().add(s2.getId());
        r3.getSensorIds().add(s3.getId());

        // Initialize reading lists
        sensorReadings.put(s1.getId(), new ArrayList<>());
        sensorReadings.put(s2.getId(), new ArrayList<>());
        sensorReadings.put(s3.getId(), new ArrayList<>());
    }
}
