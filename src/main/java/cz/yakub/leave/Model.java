package cz.yakub.leave;

import java.time.ZonedDateTime;

/**
 * Holds all primary data of the GUI application.
 */
public class Model {
    ZonedDateTime alarmTime;

    boolean onAlarmExit;

    public Model(ZonedDateTime alarmTime, boolean onAlarmExit) {
        this.alarmTime = alarmTime;
        this.onAlarmExit = onAlarmExit;
    }

    public ZonedDateTime getAlarmTime() {
        return alarmTime;
    }

    public boolean isOnAlarmExit() {
        return onAlarmExit;
    }
}
