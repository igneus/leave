package cz.yakub.leave;

public class TimeLeftFormatter {
    public static String format(int minutes) {
        int hour = 60;

        if (minutes < hour) {
            return Math.abs(minutes) + " minute" +
                    (minutes > 1 ? "s" : "");
        }

        return String.format("%02d:%02d", (minutes / hour), (minutes % hour));
    }
}
