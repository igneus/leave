package cz.yakub.leave;

import com.github.philippheuer.events4j.api.service.IEventHandler;
import cz.yakub.leave.event.AdvanceNoticeEvent;
import cz.yakub.leave.event.AlarmEvent;
import cz.yakub.leave.event.CountdownEvent;
import cz.yakub.leave.event.MinuteTickEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TrayIcon extends java.awt.TrayIcon implements ChangeListener {
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

    /**
     * Where Model reports state changes.
     * @param changeEvent
     */
    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        if (changeEvent.getSource() instanceof Model) {
            state = TrayIconState.GREEN;
            setImage(iconProvider.getGreen());
        }
    }
}
