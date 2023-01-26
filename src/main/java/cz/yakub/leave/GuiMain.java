package cz.yakub.leave;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Alternative entrypoint running the 'leave' utility as a GUI application.
 */
public class GuiMain {
    final private static String appName = "leave";
    private static ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle");

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println(messages.getString("time_missing"));
            System.exit(1);
        }

        boolean onAlarmExit = null != System.getProperty("on_alarm_exit");

        ZonedDateTime alarmTime = ZonedDateTime.now();
        try {
            alarmTime = (new TimeParser()).parse(args[0]);
        } catch (InvalidTimeStringException e) {
            System.err.println(messages.getString("time_invalid"));
            System.exit(1);
        }

        if (!SystemTray.isSupported()) {
            System.err.println("System tray not supported on this platform.");
            // TODO: instead of exiting run fallback GUI with a window
            System.exit(1);
        }

        final ZonedDateTime finalAlarmTime = alarmTime;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(finalAlarmTime, onAlarmExit);
            }
        });
    }

    private static void createAndShowGUI(ZonedDateTime alarmTime, boolean onAlarmExit) {
        final SystemTray tray = SystemTray.getSystemTray();
        final IconProvider icons = new IconProvider(tray.getTrayIconSize());

        final TrayIcon trayIcon =
                new TrayIcon(icons.getGreen());
        final PopupMenu popup = new PopupMenu();
        MenuItem timeLeftItem = new MenuItem("[WILL BE SET BY A TIMER]");
        popup.add(timeLeftItem);
        MenuItem aboutItem = new MenuItem("About");
        popup.add(aboutItem);
        popup.addSeparator();
        MenuItem exitItem = new MenuItem("Exit");
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
            System.exit(1);
        }

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "'" + appName + "' reminds you to leave in time");
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });

        regularlyUpdateTimeLeft(alarmTime, timeLeftItem, trayIcon);
        scheduleAdvanceNotices(alarmTime, trayIcon, icons);
        scheduleAlarm(alarmTime, trayIcon, icons, onAlarmExit);
        scheduleReminders(alarmTime, trayIcon);
    }

    private static void regularlyUpdateTimeLeft(ZonedDateTime alarmTime, MenuItem timeLeftItem, TrayIcon trayIcon) {
        Timer timeLeftTimer = new Timer(minutes(1), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long minutes = ZonedDateTime.now().until(alarmTime, ChronoUnit.MINUTES);
                String label = formatMinutesLeft(minutes) +
                        (minutes >= 0 ? " left" : " past the planned leave time");
                timeLeftItem.setLabel(label);
                trayIcon.setToolTip(label);
            }
        });
        timeLeftTimer.setInitialDelay(0);
        timeLeftTimer.start();
    }

    private static String formatMinutesLeft(long minutes) {
        long hour = 60;

        if (minutes < hour) {
            return Math.abs(minutes) + " minutes";
        }

        return (minutes / hour) + ":" + (minutes % hour);
    }

    private static void scheduleAdvanceNotices(ZonedDateTime alarmTime, TrayIcon trayIcon, IconProvider iconProvider) {
        int[] advanceNotices = {15, 10, 5, 2}; // TODO: duplicate code
        int[] actualAdvanceNotices =
                Arrays.stream(advanceNotices)
                        .filter(x -> alarmTime.minusMinutes(x).isAfter(ZonedDateTime.now()))
                        .toArray();

        if (actualAdvanceNotices.length == 0) {
            return;
        }

        for (int i : actualAdvanceNotices) {
            ZonedDateTime noticeTime = alarmTime.minusMinutes(i);

            Timer noticeTimer = new Timer(0, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    trayIcon.displayMessage(appName,
                            MessageFormat.format(messages.getString("advance_notice"), i), TrayIcon.MessageType.INFO);
                }
            });
            noticeTimer.setInitialDelay((int) ZonedDateTime.now().until(noticeTime, ChronoUnit.MILLIS));
            noticeTimer.setRepeats(false);
            noticeTimer.start();
        }

        // every minute update the orange icon with current time left
        Timer iconTimer = new Timer(minutes(1), null);
        iconTimer.addActionListener(new ActionListener() {
            private int i = actualAdvanceNotices[0];

            public void actionPerformed(ActionEvent e) {
                trayIcon.setImage(iconProvider.getOrange(Integer.toString(i)));

                i--;
                if (i == 0) {
                    iconTimer.stop();
                }
            }
        });
        iconTimer.setInitialDelay((int) ZonedDateTime.now().until(alarmTime.minusMinutes(actualAdvanceNotices[0]), ChronoUnit.MILLIS));
        iconTimer.start();

        // final seconds countdown
        int finalSeconds = 10;
        Timer iconTimer2 = new Timer((int) Duration.ofSeconds(1).toMillis(), null);
        iconTimer2.addActionListener(new ActionListener() {
            private int i = finalSeconds;

            public void actionPerformed(ActionEvent e) {
                trayIcon.setImage(iconProvider.getOrange(Integer.toString(i)));

                i--;
                if (i == 0) {
                    iconTimer2.stop();
                }
            }
        });
        iconTimer2.setInitialDelay((int) ZonedDateTime.now().until(alarmTime.minusSeconds(finalSeconds), ChronoUnit.MILLIS));
        iconTimer2.start();
    }

    private static void scheduleAlarm(ZonedDateTime alarmTime, TrayIcon trayIcon, IconProvider iconProvider, boolean onAlarmExit) {
        Timer alarmTimer = new Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                trayIcon.setImage(iconProvider.getRed());
                trayIcon.displayMessage(appName,
                        messages.getString("alarm"), TrayIcon.MessageType.WARNING);

                if (onAlarmExit) {
                    System.exit(0);
                }
            }
        });
        ZonedDateTime now = ZonedDateTime.now();
        if (alarmTime.isAfter(now)) {
            alarmTimer.setInitialDelay((int) now.until(alarmTime, ChronoUnit.MILLIS));
        }
        alarmTimer.setRepeats(false);
        alarmTimer.start();
    }

    private static void scheduleReminders(ZonedDateTime alarmTime, TrayIcon trayIcon) {
        Timer reminderTimer = new Timer(minutes(5), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long passed = alarmTime.until(ZonedDateTime.now(), ChronoUnit.MINUTES);
                trayIcon.displayMessage(appName,
                        MessageFormat.format(messages.getString("reminder"), passed), TrayIcon.MessageType.WARNING);
            }
        });
        reminderTimer.setInitialDelay((int) ZonedDateTime.now().until(alarmTime.plusMinutes(5), ChronoUnit.MILLIS));
        reminderTimer.start();
    }

    /**
     * Computes specified amount of minutes in the format expected by the Timer constructor.
     */
    private static int minutes(int mins) {
        return (int) Duration.ofMinutes(mins).toMillis();
    }
}
