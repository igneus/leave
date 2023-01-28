package cz.yakub.leave;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

/**
 * Alternative entrypoint running the 'leave' utility as a GUI application.
 */
public class GuiMain {
    private static ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle");

    public static void main(String[] args) {
        boolean onAlarmExit = null != System.getProperty("on_alarm_exit");

        ZonedDateTime alarmTime = null;
        try {
            alarmTime = args.length == 0 ? null : (new TimeParser()).parse(args[0]);
        } catch (InvalidTimeStringException e) {
            System.err.println(messages.getString("time_invalid"));
            System.exit(1);
        }

        Model model = new Model(alarmTime, onAlarmExit);

        if (!SystemTray.isSupported()) {
            System.err.println("System tray not supported on this platform.");
            // TODO: instead of exiting run fallback GUI with a window
            System.exit(1);
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                (new GUI(model, messages)).run();
            }
        });
    }
}
