package com.edutechit.edutechit_api.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {

    public static String getRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 60) {
            return minutes + " phút trước";
        } else if (hours < 24) {
            return hours + " giờ trước";
        } else if (days < 365) {
            return days + " ngày trước";
        } else {
            return days + " ngày trước";
        }
    }
}