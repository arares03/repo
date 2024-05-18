package com.example.siemensinternship.model;

import java.time.Instant;

public class Booking {
    private String id;
    private String name;
    private String timestamp;
    private int hotelId;
    private int roomNumber;

    public Booking() {
    }

    public Booking(String id, String name, String timestamp, int hotelId, int roomNumber) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}