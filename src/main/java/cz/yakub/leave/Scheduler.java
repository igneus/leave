package cz.yakub.leave;

import com.github.philippheuer.events4j.api.service.IEventHandler;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import cz.yakub.leave.event.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Schedules events relative to the alarm time.
 */
public class Scheduler {
    private Model model;
    private ArrayList<Timer> timers = new ArrayList<>();
    private EventManager eventManager = new EventManager();
    private IEventHandler eventHandler = new SimpleEventHandler();

    public Scheduler(Model model) {
        this.model = model;

        eventManager.registerEventHandler(eventHandler);

        timers.add(alarmTimer());
        timers.addAll(advanceNoticeTimers());
        timers.add(countdownTimer());
        timers.add(remindersTimer());
        timers.add(minuteTickTimer());
    }

    /**
     * Where listeners can subscribe to events.
     * @return
     */
    public IEventHandler getEventHandler() {
        return eventHandler;
    }

    private Timer alarmTimer() {
        Timer timer = new Timer(0, x -> eventManager.publish(new AlarmEvent()));
        ZonedDateTime now = ZonedDateTime.now();
        if (model.getAlarmTime().isAfter(now)) {
            timer.setInitialDelay((int) now.until(model.getAlarmTime(), ChronoUnit.MILLIS));
        }
        timer.setRepeats(false);
        timer.start();

        return timer;
    }

    private Collection<Timer> advanceNoticeTimers() {
        int[] advanceNotices = {15, 10, 5, 2}; // TODO: duplicate code
        int[] actualAdvanceNotices =
                Arrays.stream(advanceNotices)
                        .filter(x -> model.getAlarmTime().minusMinutes(x).isAfter(ZonedDateTime.now()))
                        .toArray();

        if (actualAdvanceNotices.length == 0) {
            return new ArrayList<>();
        }


        ArrayList<Timer> timers = new ArrayList<Timer>();

        for (int i : actualAdvanceNotices) {
            ZonedDateTime noticeTime = model.getAlarmTime().minusMinutes(i);

            Timer timer = new Timer(0, x -> eventManager.publish(new AdvanceNoticeEvent(i)));
            timer.setInitialDelay((int) ZonedDateTime.now().until(noticeTime, ChronoUnit.MILLIS));
            timer.setRepeats(false);
            timer.start();

            timers.add(timer);
        }

        return timers;
    }

    private Timer countdownTimer() {
        int finalSeconds = 10;
        Timer timer = new Timer((int) Duration.ofSeconds(1).toMillis(), null);
        timer.addActionListener(new ActionListener() {
            private int i = finalSeconds;

            public void actionPerformed(ActionEvent e) {
                eventManager.publish(new CountdownEvent(i));

                i--;
                if (i == 0) {
                    timer.stop();
                }
            }
        });
        timer.setInitialDelay((int) ZonedDateTime.now().until(model.getAlarmTime().minusSeconds(finalSeconds), ChronoUnit.MILLIS));
        timer.start();

        return timer;
    }

    private Timer remindersTimer() {
        Timer timer = new Timer(minutes(5), x -> {
            long passed = model.getAlarmTime().until(ZonedDateTime.now(), ChronoUnit.MINUTES);
            eventManager.publish(new ReminderEvent(passed));

        });
        timer.setInitialDelay((int) ZonedDateTime.now().until(model.getAlarmTime().plusMinutes(5), ChronoUnit.MILLIS));
        timer.start();

        return timer;
    }

    private Timer minuteTickTimer() {
        Timer timer = new Timer(minutes(1), x -> {
            long minutes = ZonedDateTime.now().until(model.getAlarmTime(), ChronoUnit.MINUTES);
            eventManager.publish(new MinuteTickEvent(minutes));
        });
        timer.setInitialDelay(0);
        timer.start();

        return timer;
    }

    /**
     * Computes specified amount of minutes in the format expected by the Timer constructor.
     */
    private int minutes(int mins) {
        return (int) Duration.ofMinutes(mins).toMillis();
    }
}
