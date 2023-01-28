package cz.yakub.leave;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Holds all primary data of the GUI application.
 */
public class Model {
    private ZonedDateTime alarmTime;

    private boolean onAlarmExit;

    private ArrayList<ChangeListener> changeListeners = new ArrayList<>();

    public Model(ZonedDateTime alarmTime, boolean onAlarmExit) {
        this.alarmTime = alarmTime;
        this.onAlarmExit = onAlarmExit;
    }

    public ZonedDateTime getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(ZonedDateTime alarmTime) {
        this.alarmTime = alarmTime;

        ChangeEvent event = new ChangeEvent(this);
        changeListeners.forEach(listener -> listener.stateChanged(event));
    }

    public boolean isOnAlarmExit() {
        return onAlarmExit;
    }

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }
}
