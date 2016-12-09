package com.yalin.freevideo.util;


/**
 * YaLin
 * 2016/12/9.
 */

public class TimeUtils {
    public static final int SECOND = 1000;
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;


    public static String parseTimeMillisecond(long timeMillisecond) {
        StringBuilder timeBuilder = new StringBuilder();
        int hour = (int) (timeMillisecond / HOUR);
        if (hour > 0) {
            timeBuilder.append(hour)
                    .append(":");
        }
        int min = (int) (timeMillisecond % HOUR / MINUTE);
        timeBuilder.append(min)
                .append(":");
        int seconds = (int) (timeMillisecond % HOUR % MINUTE / SECOND);
        timeBuilder.append(seconds);

        return timeBuilder.toString();
    }
}
