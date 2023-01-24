package cz.yakub.leave;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Alternative entrypoint running the 'leave' utility as a GUI application.
 */
public class GuiMain {
    final private static String appName = "leave";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please specify alarm time");
            System.exit(1);
        }

        ZonedDateTime alarmTime = ZonedDateTime.now();
        try {
            alarmTime = (new TimeParser()).parse(args[0]);
        } catch (InvalidTimeStringException e) {
            System.err.println("Time string invalid. Please provide valid time in the HHMM format.");
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
                createAndShowGUI(finalAlarmTime);
            }
        });
    }

    private static void createAndShowGUI(ZonedDateTime alarmTime) {
        final TrayIcon trayIcon =
                new TrayIcon(IconProvider.getGreen());
        final SystemTray tray = SystemTray.getSystemTray();

        final PopupMenu popup = new PopupMenu();
        MenuItem timeLeftItem = new MenuItem("[WILL BE SET BY A TIMER]");
        popup.add(timeLeftItem);
        MenuItem aboutItem = new MenuItem("About");
        popup.add(aboutItem);
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

        regularlyUpdateTimeLeft(alarmTime, timeLeftItem);
        scheduleAdvanceNotices(alarmTime, trayIcon);
        scheduleAlarm(alarmTime, trayIcon);
        scheduleReminders(alarmTime, trayIcon);
    }

    private static void regularlyUpdateTimeLeft(ZonedDateTime alarmTime, MenuItem timeLeftItem) {
        final int everyMinute = 60 * 1000;
        Timer timeLeftTimer = new Timer(everyMinute, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long minutes = ZonedDateTime.now().until(alarmTime, ChronoUnit.MINUTES);
                timeLeftItem.setLabel(Math.abs(minutes) + " minutes " +
                        (minutes >= 0 ? "left" : "past the planned leave time"));
            }
        });
        timeLeftTimer.setInitialDelay(0);
        timeLeftTimer.start();
    }

    private static void scheduleAdvanceNotices(ZonedDateTime alarmTime, TrayIcon trayIcon) {
        int[] advanceNotices = {15, 10, 5, 2}; // TODO: duplicate code
        for (int i : advanceNotices) {
            ZonedDateTime noticeTime = alarmTime.minusMinutes(i);
            if (noticeTime.isBefore(ZonedDateTime.now())) {
                continue;
            }

            Timer noticeTimer = new Timer(0, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    trayIcon.setImage(IconProvider.getOrange());
                    trayIcon.displayMessage(appName,
                            "Leaving in " + i + " minutes.", TrayIcon.MessageType.INFO);
                }
            });
            noticeTimer.setInitialDelay((int) ZonedDateTime.now().until(noticeTime, ChronoUnit.MILLIS));
            noticeTimer.setRepeats(false);
            noticeTimer.start();
        }
    }

    private static void scheduleAlarm(ZonedDateTime alarmTime, TrayIcon trayIcon) {
        Timer alarmTimer = new Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                trayIcon.setImage(IconProvider.getRed());
                trayIcon.displayMessage(appName,
                        "Time to leave!", TrayIcon.MessageType.WARNING);
            }
        });
        alarmTimer.setInitialDelay((int) ZonedDateTime.now().until(alarmTime, ChronoUnit.MILLIS));
        alarmTimer.setRepeats(false);
        alarmTimer.start();
    }

    private static void scheduleReminders(ZonedDateTime alarmTime, TrayIcon trayIcon) {
        final int everyFiveMinutes = 5 * 60 * 1000;
        Timer reminderTimer = new Timer(everyFiveMinutes, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long passed = alarmTime.until(ZonedDateTime.now(), ChronoUnit.MINUTES);
                trayIcon.displayMessage(appName,
                        "You planned to leave " + passed + " minutes ago.", TrayIcon.MessageType.WARNING);
            }
        });
        reminderTimer.setInitialDelay((int) ZonedDateTime.now().until(alarmTime.plusMinutes(5), ChronoUnit.MILLIS));
        reminderTimer.start();
    }
}
