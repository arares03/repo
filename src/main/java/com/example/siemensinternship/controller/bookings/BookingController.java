package com.example.siemensinternship.controller.bookings;

import com.example.siemensinternship.model.Booking;
import com.example.siemensinternship.model.Hotel;
import com.example.siemensinternship.model.Room;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/bookings")
    public ResponseEntity<String> getBookings() {
        try {
            // Load bookings data from JSON file
            Resource resource = resourceLoader.getResource("classpath:mock-data/bookings.json");
            String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/bookings")
    public ResponseEntity<String> addBooking(@RequestBody Booking booking) {
        try {
            // Parse timestamp string into Instant
            Instant timestamp = Instant.parse(booking.getTimestamp());

            // Load existing bookings from JSON file
            Resource resource = resourceLoader.getResource("classpath:mock-data/bookings.json");
            File file = resource.getFile();

            // Load existing hotels from JSON file
            Resource hotelResource = resourceLoader.getResource("classpath:mock-data/hotels.json");
            File hotelFile = hotelResource.getFile();

            // Read existing JSON data
            ObjectMapper objectMapper = new ObjectMapper();
// Parse JSON data from the content string
            String content = new String(Files.readAllBytes(Paths.get(hotelResource.getURI())));
            List<Hotel> hotels = objectMapper.readValue(content, new TypeReference<List<Hotel>>() {});

            // Check if the hotel with the given hotelId exists
            boolean hotelExists = false;
            for (Hotel hotel : hotels) {
                if (hotel.getId() == booking.getHotelId()) {
                    hotelExists = true;
                    // Check if the room with the given roomNumber exists in the hotel
                    boolean roomExists = false;
                    for (Room room : hotel.getRooms()) {
                        if (room.getRoomNumber() == booking.getRoomNumber()) {
                            roomExists = true;
                            // Check if the room is available
                            if (room.isAvailable()) {
                                // Add the booking to the room and mark it as booked
                                // Add the new booking to the list
                                List<Booking> bookings = objectMapper.readValue(file, new TypeReference<List<Booking>>() {});
                                bookings.add(booking);
                                // Write the updated bookings and hotel data back to the JSON files

                                // Return a success message with the booking details
                                String message = "Booking added successfully: " + booking.toString();
                                room.setAvailable(false);
                                objectMapper.writeValue(hotelFile, hotels);
                                System.out.println(hotel.getId());
                                return ResponseEntity.status(HttpStatus.CREATED).body(message);
                            } else {
                                return ResponseEntity.badRequest().body("Room " + booking.getRoomNumber() + " is already booked");
                            }
                        }
                    }
                    return ResponseEntity.badRequest().body("Room " + booking.getRoomNumber() + " does not exist in hotel " + booking.getHotelId());
                }
            }
            return ResponseEntity.badRequest().body("Hotel " + booking.getHotelId() + " does not exist");

        } catch (DateTimeParseException e) {
            // Handle parsing exception
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to parse timestamp");
        } catch (IOException e) {
            // Handle any other IO exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add booking");
        }
    }
}