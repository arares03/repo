package com.example.siemensinternship.controller.hotels;

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

@RestController
@RequestMapping("/api")
public class HotelsController {

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/hotels")
    public ResponseEntity<String> getMockData(@RequestParam(name = "latitude") double latitude,
                                              @RequestParam(name = "longitude") double longitude,
                                              @RequestParam(name = "radius") double radius) {
        try {
            // Use latitude, longitude, and radius in your logic
            String queryParams = String.format("latitude=%f, longitude=%f, radius=%f", latitude, longitude, radius);
            System.out.println("Latitude: " + latitude);
            System.out.println("Longitude: " + longitude);
            System.out.println("Radius: " + radius);

            ///aici trebe facut o functie sa faca handle la latitude, longitude, radius. in alta clasa
            Resource resource = resourceLoader.getResource("classpath:mock-data/hotels.json");
            String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            return new ResponseEntity<>(content + "\nQuery Parameters: " + queryParams, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
