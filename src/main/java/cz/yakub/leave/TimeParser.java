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

    /**
     * Transforms LocalTime to the next valid alarm time.
     *
     * @param time
     * @return
     */
    public ZonedDateTime fromLocalTime(LocalTime time) {
        ZonedDateTime dateTime =
                this.currentTime
                        .withHour(time.getHour())
                        .withMinute(time.getMinute())
                        .withSecond(0);

        if (dateTime.isBefore(this.currentTime)) {
            return dateTime.plusDays(1);
        }

        return dateTime;
    }

    private ZonedDateTime parseRelative(String timeString) {
        LocalTime parsedTime = parseLocalTime(timeString);

        return this.currentTime
                .plusHours(parsedTime.getHour())
                .plusMinutes(parsedTime.getMinute())
                .withSecond(0);
    }

    private ZonedDateTime parseAbsolute(String timeString) {
        return fromLocalTime(parseLocalTime(timeString));
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
