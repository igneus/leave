package cz.yakub.leave;

import java.time.ZonedDateTime;

public class TimeParser {
    private ZonedDateTime currentTime;

    public TimeParser() {
        this.currentTime = ZonedDateTime.now();
    }

    public TimeParser(ZonedDateTime currentTime) {
        this.currentTime = currentTime;
    }

    public ZonedDateTime parse(String timeString) {
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

        ZonedDateTime time = this.currentTime.withHour(hour).withMinute(minute);

        if (time.isBefore(this.currentTime)) {
            return time.plusDays(1);
        }

        return time;
    }
}
