package cz.yakub.leave.event;

public class AdvanceNoticeEvent extends TimerEvent {
    private int minutesLeft;

    public AdvanceNoticeEvent(int minutesLeft) {
        this.minutesLeft = minutesLeft;
    }

    public int getMinutesLeft() {
        return minutesLeft;
    }
}
