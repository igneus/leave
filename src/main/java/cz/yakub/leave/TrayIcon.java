package cz.yakub.leave;

import com.github.philippheuer.events4j.api.service.IEventHandler;
import cz.yakub.leave.event.AdvanceNoticeEvent;
import cz.yakub.leave.event.AlarmEvent;
import cz.yakub.leave.event.CountdownEvent;
import cz.yakub.leave.event.MinuteTickEvent;

public class TrayIcon extends java.awt.TrayIcon {
    private IconProvider iconProvider;
    private TrayIconState state = TrayIconState.GREEN;

    public TrayIcon(IconProvider iconProvider) {
        super(iconProvider.getGreen());

        this.iconProvider = iconProvider;
    }

    /**
     * Subscribe to all events affecting the tray icon.
     * @param eventHandler
     */
    public void subscribe(IEventHandler eventHandler) {
        eventHandler.onEvent(AdvanceNoticeEvent.class, event -> {
            state = TrayIconState.ORANGE;
            setImage(iconProvider.getOrange(Integer.toString(event.getMinutesLeft())));
        });

        // orange minutes
        eventHandler.onEvent(MinuteTickEvent.class, event -> {
            if (state == TrayIconState.ORANGE && event.getMinutesLeft() > 0) {
                setImage(iconProvider.getOrange(Integer.toString((int) event.getMinutesLeft())));
            }
        });

        // final seconds countdown
        eventHandler.onEvent(CountdownEvent.class,
                event -> setImage(iconProvider.getOrange(Integer.toString(event.getSecondsLeft()))));

        // alarm
        eventHandler.onEvent(AlarmEvent.class, event -> {
            state = TrayIconState.RED;
            setImage(iconProvider.getRed());
        });
    }
}
