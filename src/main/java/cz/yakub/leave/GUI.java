package cz.yakub.leave;

import cz.yakub.leave.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class GUI {
    final private static String appName = "leave";

    private Model model;
    private Scheduler scheduler;
    private ResourceBundle messages;

    public GUI(Model model, ResourceBundle messages) {
        this.model = model;
        this.messages = messages;
        this.scheduler = new Scheduler(this.model);
        this.model.addChangeListener(this.scheduler);
    }

    public void run() {
        final SystemTray tray = SystemTray.getSystemTray();
        final IconProvider icons = new IconProvider(tray.getTrayIconSize());

        final TrayIcon trayIcon = new TrayIcon(icons);
        trayIcon.subscribe(scheduler.getEventHandler());
        model.addChangeListener(trayIcon);
        trayIcon.stateChanged(new ChangeEvent(model)); // TODO: dirty way to make icon refresh just in case time is null

        final Menu menu = new Menu(appName, model);
        model.addChangeListener(menu);
        trayIcon.setPopupMenu(menu);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
            System.exit(1);
        }

        regularlyUpdateTimeLeft(menu, trayIcon);
        scheduleAdvanceNotices(trayIcon, icons);
        scheduleAlarm(trayIcon, icons);
        scheduleReminders(trayIcon);
    }

    private void regularlyUpdateTimeLeft(Menu menu, TrayIcon trayIcon) {
        scheduler.getEventHandler().onEvent(MinuteTickEvent.class, event -> {
            String label = formatMinutesLeft(event.getMinutesLeft()) +
                    (event.getMinutesLeft() >= 0 ? " left" : " past the planned leave time");
            menu.setTimeLeft(label);
            trayIcon.setToolTip(label);
        });
    }

    private String formatMinutesLeft(long minutes) {
        long hour = 60;

        if (minutes < hour) {
            return Math.abs(minutes) + " minutes";
        }

        return (minutes / hour) + ":" + (minutes % hour);
    }

    private void scheduleAdvanceNotices(TrayIcon trayIcon, IconProvider iconProvider) {
        scheduler.getEventHandler().onEvent(AdvanceNoticeEvent.class, event -> {
            trayIcon.displayMessage(appName,
                    MessageFormat.format(messages.getString("advance_notice"), event.getMinutesLeft()), TrayIcon.MessageType.INFO);
        });
    }

    private void scheduleAlarm(TrayIcon trayIcon, IconProvider iconProvider) {
        scheduler.getEventHandler().onEvent(AlarmEvent.class, event -> {
            trayIcon.displayMessage(appName,
                    messages.getString("alarm"), TrayIcon.MessageType.WARNING);

            if (model.isOnAlarmExit()) {
                System.exit(0);
            }
        });
    }

    private void scheduleReminders(TrayIcon trayIcon) {
        scheduler.getEventHandler().onEvent(ReminderEvent.class, event -> {
            trayIcon.displayMessage(appName,
                    MessageFormat.format(messages.getString("reminder"), event.getMinutesPassed()), TrayIcon.MessageType.WARNING);
        });
    }
}
