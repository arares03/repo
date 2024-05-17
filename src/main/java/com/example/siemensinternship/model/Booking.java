package com.example.siemensinternship.model;

import java.time.Instant;

public class Booking {
    private String id;
    private String name;
    private String timestamp;

    // Constructors, getters, and setters

    public Booking() {
    }

    public Booking(String id, String name, String timestamp) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
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
}
