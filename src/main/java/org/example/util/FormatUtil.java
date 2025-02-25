package org.example.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class FormatUtil {
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");

    public static String instantToLocalDateString(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDate.ofInstant(instant, DEFAULT_ZONE).toString();
    }

    public static Instant localDateStringToInstant(String date) {
        if (date == null) {
            return null;
        }
        return LocalDate.parse(date).atStartOfDay(DEFAULT_ZONE).toInstant();
    }
}
