package com.smartcampus.resources;

import com.smartcampus.DataStore;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PART 2 - Room Management
 *
 * GET    /api/v1/rooms          -> List all rooms
 * POST   /api/v1/rooms          -> Create new room
 * GET    /api/v1/rooms/{roomId} -> Get specific room
 * DELETE /api/v1/rooms/{roomId} -> Delete room (with sensor safety check)
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore store = DataStore.getInstance();

    /**
     * PART 2.1 - GET all rooms
     * Returns full room objects (ID only vs full objects - bandwidth consideration)
     */
    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(store.getRooms().values());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("count", roomList.size());
        response.put("rooms", roomList);

        return Response.ok(response).build();
    }

    /**
     * PART 2.1 - POST create new room
     * Returns 201 Created + Location header
     */
    @POST
    public Response createRoom(Room room) {
        // Validation
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "Room ID is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (room.getName() == null || room.getName().trim().isEmpty()) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "Room name is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        // Duplicate check
        if (store.getRoomById(room.getId()) != null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Conflict");
            error.put("message", "Room with ID '" + room.getId() + "' already exists");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        store.addRoom(room);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Room created successfully");
        response.put("room", room);

        // 201 Created with Location header
        return Response.status(Response.Status.CREATED)
                .header("Location", "/api/v1/rooms/" + room.getId())
                .entity(response)
                .build();
    }

    /**
     * PART 2.1 - GET room by ID
     */
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.getRoomById(roomId);

        if (room == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Not Found");
            error.put("message", "Room with ID '" + roomId + "' not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(room).build();
    }

    /**
     * PART 2.2 - DELETE room
     *
     * Business Logic: A room cannot be deleted if it still has sensors.
     * Idempotent: When attempting to delete the same room ID multiple times:
     *   - 1st call: 200 OK (delete success)
     *   - 2nd+ calls: 404 Not Found (already gone - no server state change)
     *
     * IMPORTANT: DELETE is idempotent when the ROOM HAS NO SENSORS.
     * Once deleted, calling DELETE again returns 404 - the outcome (room not existing) is the same.
     * This is acceptable idempotent behavior.
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoomById(roomId);

        // Room not found
        if (room == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Not Found");
            error.put("message", "Room with ID '" + roomId + "' not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // BUSINESS LOGIC: Cannot delete if sensors exist - throws 409 Conflict
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId, room.getSensorIds().size());
        }

        store.deleteRoom(roomId);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Room '" + roomId + "' successfully deleted");
        response.put("roomId", roomId);

        return Response.ok(response).build();
    }
}
