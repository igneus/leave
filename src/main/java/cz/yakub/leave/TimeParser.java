package cz.yakub.leave;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses time strings accepted by the CLI.
 */
public class TimeParser {
    private ZonedDateTime currentTime;

    public TimeParser() {
        this(ZonedDateTime.now());
    }

    public TimeParser(ZonedDateTime currentTime) {
        this.currentTime = currentTime;
    }

    public ZonedDateTime parse(String timeString) {
        if (timeString.substring(0, 1).equals("+")) {
            return parseRelative(timeString.substring(1));
        }

        return parseAbsolute(timeString);
    }

    private ZonedDateTime parseRelative(String timeString) {
        LocalTime parsedTime = parseLocalTime(timeString);

        return this.currentTime
                .plusHours(parsedTime.getHour())
                .plusMinutes(parsedTime.getMinute())
                .withSecond(0);
    }

    private ZonedDateTime parseAbsolute(String timeString) {
        LocalTime parsedTime = parseLocalTime(timeString);
        ZonedDateTime time =
                this.currentTime
                        .withHour(parsedTime.getHour())
                        .withMinute(parsedTime.getMinute())
                        .withSecond(0);

        if (time.isBefore(this.currentTime)) {
            return time.plusDays(1);
        }

        return time;
    }

    private LocalTime parseLocalTime(String timeString) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HHmm");

        try {
            return LocalTime.parse(timeString, format);
        } catch (DateTimeParseException e) {
            throw new InvalidTimeStringException();
        }
    }
}
