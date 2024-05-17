package com.example.siemensinternship.controller.hotels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HotelsController {

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/hotels")
    public ResponseEntity<List<JsonNode>> getMockData(@RequestParam(name = "latitude") double userLatitude,
                                              @RequestParam(name = "longitude") double userLongitude,
                                              @RequestParam(name = "radius") double radius) {
        try {
            Resource resource = resourceLoader.getResource("classpath:mock-data/hotels.json");
            String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));

            // Parse JSON content using ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            // Iterate through hotels and get latitude and longitude for each hotel
            List<JsonNode> qualifyingHotels = new ArrayList<>();
            for (JsonNode hotel : root) {
                double hotelLatitude = hotel.get("latitude").asDouble();
                double hotelLongitude = hotel.get("longitude").asDouble();

                double dist = computeDistance(userLatitude, userLongitude, hotelLatitude, hotelLongitude);

                if(dist < radius) {
                    qualifyingHotels.add(hotel);
                }
            }

            return new ResponseEntity<>(qualifyingHotels, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private double computeDistance(double userLatitude, double userLongitude, double hotelLatitude, double hotelLongitude) {
        // Radius of the Earth in kilometers
        double earthRadius = 6371; // in kilometers

        // Convert latitude and longitude from degrees to radians
        double userLatRad = Math.toRadians(userLatitude);
        double userLongRad = Math.toRadians(userLongitude);
        double hotelLatRad = Math.toRadians(hotelLatitude);
        double hotelLongRad = Math.toRadians(hotelLongitude);

        // Calculate the differences between the coordinates
        double deltaLat = hotelLatRad - userLatRad;
        double deltaLong = hotelLongRad - userLongRad;

        // Calculate the distance using the Haversine formula
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(userLatRad) * Math.cos(hotelLatRad) *
                        Math.pow(Math.sin(deltaLong / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        return distance * 1000; // Distance between the user and the hotel in meters
    }

}
