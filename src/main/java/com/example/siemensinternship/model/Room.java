package com.example.siemensinternship.model;


public class Room {
    private int roomNumber;
    private int type;
    private double price;
    private boolean isAvailable;

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    public boolean getIsAvailable(){
        return isAvailable;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public Room(boolean isAvailable, double price, int type, int roomNumber) {
        this.isAvailable = isAvailable;
        this.price = price;
        this.type = type;
        this.roomNumber = roomNumber;
    }

    public Room() {
    }

    public Room(int roomNumber, int type, double price, boolean isAvailable) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.isAvailable = isAvailable;
    }

}