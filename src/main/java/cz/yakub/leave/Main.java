package cz.yakub.leave;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            System.err.println("Please specify time");
            System.exit(1);
        }

        ZonedDateTime alarmTime = (new TimeParser()).parse(args[0]);
        Main.displayAlarmTime(alarmTime);

        long milliseconds = ZonedDateTime.now().until(alarmTime, ChronoUnit.MILLIS);
        Thread.sleep(milliseconds);

        // TODO: reminders that time to leave is approaching

        System.out.println("Time to leave!");

        // TODO: reminders that time to leave has been passed
    }

    private static void displayAlarmTime(ZonedDateTime alarmTime) {
        if (alarmTime.getDayOfMonth() != ZonedDateTime.now().getDayOfMonth()) {
            System.out.println("WARNING: setting alarm out of bounds of the current day");
        }
        System.out.println("Alarm set for " + alarmTime.format(DateTimeFormatter.RFC_1123_DATE_TIME) + ".");
    }
}
