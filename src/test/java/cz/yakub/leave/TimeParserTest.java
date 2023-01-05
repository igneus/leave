package cz.yakub.leave;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeParserTest {
    private ZonedDateTime currentTime;
    private TimeParser subject;

    @Before
    public void setUp() {
        this.currentTime = ZonedDateTime.of(2001, 1, 1, 9, 0, 0, 0, ZoneId.of("UTC"));
        this.subject = new TimeParser();
    }

    @Test
    public void returnsZonedDateTime() {
        assertEquals(this.currentTime.withHour(10), this.subject.parse("1000"));
    }
}
