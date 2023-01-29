package cz.yakub.leave;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TimeLeftFormatterTest {
    @Test
    public void one() {
        assertEquals("1 minute", TimeLeftFormatter.format(1));
    }

    @Test
    public void singleDigitMinutes() {
        assertEquals("2 minutes", TimeLeftFormatter.format(2));
    }

    @Test
    public void oneHour() {
        assertEquals("01:00", TimeLeftFormatter.format(60));
    }

    @Test
    public void threeHoursThreeMinutes() {
        assertEquals("03:03", TimeLeftFormatter.format(183));
    }
}
