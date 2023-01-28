package cz.yakub.leave;

import com.github.lgooddatepicker.components.TimePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

public class SettingsFrame extends JFrame {
    public SettingsFrame(String title, Model model) {
        super(title);

        setLayout(new FlowLayout());

        JLabel label = new JLabel("Alarm time");
        add(label);

        // TODO: display notice if the selected time is past midnight
        TimePicker timePicker = new TimePicker();
        timePicker.setTime(model.getAlarmTime().toLocalTime());
        add(timePicker);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(event -> {
            LocalTime time = timePicker.getTime();
            model.setAlarmTime((new TimeParser()).fromLocalTime(time));
            setVisible(false);
        });
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> setVisible(false));
        add(cancelButton);
    }
}
