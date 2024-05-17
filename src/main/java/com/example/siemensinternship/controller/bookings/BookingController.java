package com.example.siemensinternship.controller.bookings;

import com.example.siemensinternship.model.Booking;
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

            // Create a new booking with the parsed timestamp

            // Rest of your logic (e.g., saving to JSON file)
            // Load existing bookings from JSON file
            Resource resource = resourceLoader.getResource("classpath:mock-data/bookings.json");
            File file = resource.getFile();

            // Read existing JSON data
            ObjectMapper objectMapper = new ObjectMapper();
            List<Booking> bookings = objectMapper.readValue(file, new TypeReference<List<Booking>>() {});

            // Add the new booking to the list
            bookings.add(booking);

            // Write the updated list to a temporary file
            File tempFile = File.createTempFile("bookings_temp", ".json");
            objectMapper.writeValue(tempFile, bookings);

            // Replace the original file with the temporary one
            FileCopyUtils.copy(tempFile, file);

            // Delete the temporary file
            tempFile.delete();

            // Return a success message with the booking details
            String message = "Booking added successfully: " + booking.toString();
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (DateTimeParseException e) {
            // Handle parsing exception
            e.printStackTrace();
            return new ResponseEntity<>("Failed to parse timestamp", HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            // Handle any other IO exceptions
            e.printStackTrace();
            return new ResponseEntity<>("Failed to add booking", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}