package ru.practicum.dto;

import java.time.format.DateTimeFormatter;

public class TimeFormat {
        public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
}
