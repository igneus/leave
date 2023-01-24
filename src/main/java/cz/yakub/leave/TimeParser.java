package cz.yakub.leave;

import java.time.ZonedDateTime;

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
        int hour, minute;

        try {
            hour = Integer.parseInt(timeString.substring(0, 2));
            minute = Integer.parseInt(timeString.substring(2, 4));
        } catch (NumberFormatException e) {
            throw new InvalidTimeStringException();
        }

        if (hour < 0 || minute < 0) {
            throw new InvalidTimeStringException();
        }

        return this.currentTime.plusHours(hour).plusMinutes(minute).withSecond(0);
    }

    private ZonedDateTime parseAbsolute(String timeString) {
        int hour, minute;

        try {
            hour = Integer.parseInt(timeString.substring(0, 2));
            minute = Integer.parseInt(timeString.substring(2, 4));
        } catch (NumberFormatException e) {
            throw new InvalidTimeStringException();
        }

        if (hour < 0 || hour >= 24 ||
                minute < 0 || minute >= 60) {
            throw new InvalidTimeStringException();
        }

        ZonedDateTime time = this.currentTime.withHour(hour).withMinute(minute).withSecond(0);

        if (time.isBefore(this.currentTime)) {
            return time.plusDays(1);
        }

        return time;
    }
}
