package cz.yakub.leave.event;

public class ReminderEvent extends TimerEvent {
    private long minutesPassed;

    public ReminderEvent(long minutesPassed) {
        this.minutesPassed = minutesPassed;
    }

    public long getMinutesPassed() {
        return minutesPassed;
    }
}
