package cz.yakub.leave;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please specify time");
            System.exit(1);
        }

        ZonedDateTime alarmTime = (new TimeParser()).parse(args[0]);

        if (alarmTime.getDayOfMonth() != ZonedDateTime.now().getDayOfMonth()) {
            System.out.println("WARNING: setting alarm out of bounds of the current day");
        }
        System.out.println("Alarm set for " + alarmTime.format(DateTimeFormatter.RFC_1123_DATE_TIME) + ".");

        // TODO: actually wait and set the alarm off
    }
}
