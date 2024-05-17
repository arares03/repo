package com.example.siemensinternship.controller.bookings;

import java.time.Duration;
import java.time.Instant;

public class BookingUtils {

    public static boolean isFirstTimestampBeforeSecond(Instant firstTimestamp, Instant secondTimestamp) {
        // Calculate the duration between the two timestamps
        Duration duration = Duration.between(firstTimestamp, secondTimestamp);

        // Check if the duration is at least 2 hours
        return duration.toHours() >= 2;
    }

    ///cred ca aici ar merGE functiile de handle

}