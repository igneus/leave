package cz.yakub.leave.event;

public class CountdownEvent extends TimerEvent {
    private int secondsLeft;

    public CountdownEvent(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }
}
