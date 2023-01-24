package cz.yakub.leave;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Implementation of the traditional CLI 'leave' utility.
 * Default entrypoint of the application.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            System.err.println("Please specify alarm time");
            System.exit(1);
        }

        ZonedDateTime alarmTime = ZonedDateTime.now();
        try {
            alarmTime = (new TimeParser()).parse(args[0]);
        } catch (InvalidTimeStringException e) {
            System.err.println("Time string invalid. Please provide valid time in the HHMM format.");
            System.exit(1);
        }
        displayAlarmTime(alarmTime);

        waitForAlarm(alarmTime);
        System.out.println("Time to leave!");
        displayReminders();
    }

    private static void displayAlarmTime(ZonedDateTime alarmTime) {
        if (alarmTime.getDayOfMonth() != ZonedDateTime.now().getDayOfMonth()) {
            System.out.println("WARNING: alarm time past midnight");
        }
        System.out.println("Alarm set for " + alarmTime.format(DateTimeFormatter.RFC_1123_DATE_TIME) + ".");
    }

    private static void waitForAlarm(ZonedDateTime alarmTime) throws InterruptedException {
        int[] advanceNotices = {15, 10, 5, 2};
        for (int i: advanceNotices) {
            if (alarmTime.minusMinutes(i).isBefore(ZonedDateTime.now())) {
                continue;
            }

            sleepUntil(alarmTime.minusMinutes(i));
            System.out.println("Leaving in " + i + " minutes.");
        }

        sleepUntil(alarmTime);
    }

    private static void sleepUntil(ZonedDateTime alarmTime) throws InterruptedException {
        long milliseconds = ZonedDateTime.now().until(alarmTime, ChronoUnit.MILLIS);
        Thread.sleep(milliseconds);
    }

    private static void displayReminders() throws InterruptedException {
        int sleepMinutes = 5, passed = 0;
        while (true) {
            Thread.sleep(sleepMinutes * 60 * 1000);
            passed += sleepMinutes;
            System.out.println("You planned to leave " + passed + " minutes ago.");
        }
    }
}
