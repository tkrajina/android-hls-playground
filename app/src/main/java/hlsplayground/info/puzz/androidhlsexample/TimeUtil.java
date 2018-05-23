package hlsplayground.info.puzz.androidhlsexample;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    private TimeUtil() {}

    public static String formatDurationToHHMMSS(long duration) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long remainingSeconds = Math.abs(seconds % 60);
        long mins = seconds / 60;
        long remainingMins = Math.abs(mins % 60);
        long hours = mins / 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, remainingMins, remainingSeconds);
    }
}
