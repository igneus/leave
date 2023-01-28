package cz.yakub.leave;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends java.awt.PopupMenu implements ChangeListener {
    private String appName;
    private Model model;

    MenuItem timeLeftItem;

    public Menu(String appName, Model model) {
        super();

        this.appName = appName;
        this.model = model;

        refreshItems();
    }

    public void setTimeLeft(String timeLeft) {
        timeLeftItem.setLabel(timeLeft);
    }

    /**
     * Where Model reports state changes.
     * @param changeEvent
     */
    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        if (changeEvent.getSource() != model) {
            throw new RuntimeException("ChangeEvent from an unexpected source received");
        }

        refreshItems();
    }

    private void refreshItems() {
        this.removeAll();

        timeLeftItem = new MenuItem("[WILL BE SET BY A TIMER]");
        timeLeftItem.setEnabled(false);
        if (model.getAlarmTime() != null) {
            add(timeLeftItem);
        }

        MenuItem settingsItem = new MenuItem("Settings");
        add(settingsItem);
        MenuItem aboutItem = new MenuItem("About");
        add(aboutItem);
        addSeparator();
        MenuItem exitItem = new MenuItem("Exit");
        add(exitItem);

        settingsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                (new SettingsFrame(appName + " : Settings", model)).setVisible(true);
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "'" + appName + "' reminds you to leave in time");
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
