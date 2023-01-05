package cz.yakub.leave;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        ZonedDateTime alarmTime = ZonedDateTime.now(); // TODO: fake hardcoded alarm time

        System.out.println("Alarm set for " + alarmTime.format(DateTimeFormatter.RFC_1123_DATE_TIME) + ".");

        // TODO: actually wait and set the alarm off
    }
}
