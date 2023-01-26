package cz.yakub.leave.event;

public class MinuteTickEvent extends TimerEvent {
    private long minutesLeft;

    public MinuteTickEvent(long minutesLeft) {
        this.minutesLeft = minutesLeft;
    }

    public long getMinutesLeft() {
        return minutesLeft;
    }
}
