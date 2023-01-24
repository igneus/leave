package cz.yakub.leave;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeParserTest {
    private ZonedDateTime currentTime, zeroSecondsTime;
    private TimeParser subject;

    @Before
    public void setUp() {
        this.currentTime = ZonedDateTime.of(2001, 1, 1, 9, 5, 28, 0, ZoneId.of("UTC"));
        this.subject = new TimeParser(this.currentTime);

        this.zeroSecondsTime = this.currentTime.withSecond(0);
    }

    @Test
    public void timeAfterCurrentTime() {
        assertEquals(
                this.zeroSecondsTime
                        .withHour(11)
                        .withMinute(22),
                this.subject.parse("1122")
        );
    }

    @Test
    public void timeBeforeCurrentTime() {
        assertEquals(
                this.zeroSecondsTime
                        .plusDays(1) // 05:07 never more available today, assume yesterday
                        .withHour(5)
                        .withMinute(7),
                this.subject.parse("0507")
        );
    }

    @Test
    public void minValidTime() {
        assertEquals(
                this.zeroSecondsTime
                        .plusDays(1)
                        .withHour(0)
                        .withMinute(0),
                this.subject.parse("0000")
        );
    }

    @Test
    public void maxValidTime() {
        assertEquals(
                this.zeroSecondsTime
                        .withHour(23)
                        .withMinute(59),
                this.subject.parse("2359")
        );
    }

    @Test(expected = InvalidTimeStringException.class)
    public void invalidHour() {
        this.subject.parse("2500");
    }

    @Test(expected = InvalidTimeStringException.class)
    public void invalidMinute() {
        this.subject.parse("0160");
    }

    @Test(expected = InvalidTimeStringException.class)
    public void nonNumericTimeString() {
        this.subject.parse("abcd");
    }

    @Test
    public void relativeTime() {
        assertEquals(
                this.zeroSecondsTime
                        .plusHours(1)
                        .plusMinutes(1),
                this.subject.parse("+0101")
        );
    }

    @Test
    public void minRelativeTime() {
        assertEquals(
                this.zeroSecondsTime,
                this.subject.parse("+0000")
        );
    }

    @Test
    public void maxRelativeTime() {
        assertEquals(
                this.zeroSecondsTime
                        .plusHours(23)
                        .plusMinutes(59),
                this.subject.parse("+2359")
        );
    }
}
