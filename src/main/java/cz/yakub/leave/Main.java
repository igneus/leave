package cz.yakub.leave;

import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/**
 * Implementation of the traditional CLI 'leave' utility.
 * Default entrypoint of the application.
 */
public class Main {
    private static ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle");

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            System.err.println(messages.getString("time_missing"));
            System.exit(1);
        }

        boolean onAlarmExit = null != System.getProperty("on_alarm_exit");

        ZonedDateTime alarmTime = ZonedDateTime.now();
        try {
            alarmTime = (new TimeParser()).parse(args[0]);
        } catch (InvalidTimeStringException e) {
            System.err.println(messages.getString("time_invalid"));
            System.exit(1);
        }
        displayAlarmTime(alarmTime);

        waitForAlarm(alarmTime);
        System.out.println(messages.getString("alarm"));
        if (onAlarmExit) {
            return;
        }
        displayReminders();
    }

    private static void displayAlarmTime(ZonedDateTime alarmTime) {
        if (alarmTime.getDayOfMonth() != ZonedDateTime.now().getDayOfMonth()) {
            System.out.println(messages.getString("time_tomorrow"));
        }
        System.out.println(MessageFormat.format(messages.getString("alarm_set"), alarmTime.format(DateTimeFormatter.RFC_1123_DATE_TIME)));
    }

    private static void waitForAlarm(ZonedDateTime alarmTime) throws InterruptedException {
        int[] advanceNotices = {15, 10, 5, 2};
        ZonedDateTime now = ZonedDateTime.now();
        for (int i: advanceNotices) {
            if (alarmTime.minusMinutes(i).isBefore(now)) {
                continue;
            }

            sleepUntil(alarmTime.minusMinutes(i));
            System.out.println(MessageFormat.format(messages.getString("advance_notice"), i));
        }

        sleepUntil(alarmTime);
    }

    private static void sleepUntil(ZonedDateTime alarmTime) throws InterruptedException {
        ZonedDateTime now = ZonedDateTime.now();
        long milliseconds = now.until(alarmTime, ChronoUnit.MILLIS);
        if (milliseconds <= 0) {
            return;
        }
        Thread.sleep(milliseconds);
    }

    private static void displayReminders() throws InterruptedException {
        int sleepMinutes = 5, passed = 0;
        while (true) {
            Thread.sleep(sleepMinutes * 60 * 1000);
            passed += sleepMinutes;
            System.out.println(MessageFormat.format(messages.getString("reminder"), passed));
        }
    }
}
