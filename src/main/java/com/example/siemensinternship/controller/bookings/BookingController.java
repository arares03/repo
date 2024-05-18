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
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
            Instant timestamp = Instant.parse(booking.getTimestamp());

            Resource resource = resourceLoader.getResource("classpath:mock-data/bookings.json");
            File file = resource.getFile();

            Resource hotelResource = resourceLoader.getResource("classpath:mock-data/hotels.json");
            File hotelFile = hotelResource.getFile();

            // Read existing JSON data
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse JSON data from the content string
            String content = new String(Files.readAllBytes(Paths.get(hotelResource.getURI())));
            List<Hotel> hotels = objectMapper.readValue(content, new TypeReference<List<Hotel>>() {});

            Hotel hotel = checkIfHotelExists(hotels, booking.getHotelId());
            if (hotel != null) {
                    Room room = checkIfRoomExists(hotel.getRooms(), booking.getRoomNumber());
                    if (room != null) {
                            if (room.isAvailable()) {
                                List<Booking> bookings = objectMapper.readValue(file, new TypeReference<List<Booking>>() {});
                                bookings.add(booking);

                                objectMapper.writeValue(file, bookings);

                                String message = "Booking added successfully: " + booking.toString();
                                room.setAvailable(false);
                                objectMapper.writeValue(hotelFile, hotels);

                                return ResponseEntity.status(HttpStatus.CREATED).body(message);
                            } else {
                                return ResponseEntity.badRequest().body("Room " + booking.getRoomNumber() + " is already booked");
                            }
                    }
                    return ResponseEntity.badRequest().body("Room " + booking.getRoomNumber() + " does not exist in hotel " + booking.getHotelId());
            }
            return ResponseEntity.badRequest().body("Hotel " + booking.getHotelId() + " does not exist");

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to parse timestamp");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add booking");
        }
    }

    private Hotel checkIfHotelExists(List<Hotel> hotels, int hotelID) {
        for(Hotel hotel: hotels) {
            if(hotel.getId() == hotelID) {
                return hotel;
            }
        }
        return null;
    }

    private Room checkIfRoomExists(List<Room> rooms, int roomNumber) {
        for(Room room: rooms) {
            if(room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    @PatchMapping("/bookings/{bookingId}/{timestamp}")
    public ResponseEntity<String> cancelBooking(@PathVariable String bookingId, @PathVariable String timestamp) {
        try {
            // Parse timestamp string into Instant
            Instant providedTimestamp = Instant.parse(timestamp);

            // Load existing bookings from JSON file
            Resource resource = resourceLoader.getResource("classpath:mock-data/bookings.json");
            File file = resource.getFile();

            // Read existing JSON data
            ObjectMapper objectMapper = new ObjectMapper();
            List<Booking> bookings = objectMapper.readValue(file, new TypeReference<List<Booking>>() {});
            Booking booking = checkIfBookingExists(bookings, bookingId);

            if (booking != null) {
                Instant bookingTimestamp = Instant.parse(booking.getTimestamp());
                if (isFirstTimestampBeforeSecond(bookingTimestamp, providedTimestamp)) {
                    // Provided timestamp is at least 2 hours after the booking's timestamp, proceed with canceling the booking
                    bookings.remove(booking);
                    objectMapper.writeValue(file, bookings);

                    return ResponseEntity.ok("Booking with ID " + bookingId + " canceled successfully");
                } else {
                    // Provided timestamp is not at least 2 hours after the booking's timestamp, return a bad request response
                    return ResponseEntity.badRequest().body("Provided timestamp must be at least 2 hours after the booking's timestamp");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to parse timestamp");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to cancel booking");
        }
    }

    private boolean isFirstTimestampBeforeSecond(Instant firstTimestamp, Instant secondTimestamp) {
        Duration duration = Duration.between(firstTimestamp, secondTimestamp);
        System.out.println(duration);
        return duration.toHours() <= 2;
    }

    private Booking checkIfBookingExists(List<Booking> bookings, String bookingId) {
        for(Booking booking : bookings) {
            if(booking.getId().equals(bookingId)) {
                return booking;
            }
        }
        return null;
    }

    @DeleteMapping("/bookings")
    public ResponseEntity<String> deleteAllBookings() { //functie creata pentru testare!!
        try {
            Resource resource = resourceLoader.getResource("classpath:mock-data/bookings.json");
            File file = resource.getFile();

            List<Booking> bookings = Collections.emptyList();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, bookings);

            return ResponseEntity.ok("All bookings deleted successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete bookings");
        }
    }


}