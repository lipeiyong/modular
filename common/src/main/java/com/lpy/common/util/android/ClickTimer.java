package com.lpy.common.util.android;

/**
 * @author lipeiyong
 * @date on 17-11-17  上午10:19
 */
public class ClickTimer {
    /**
     * ms
     */
    private static final long TIME_OUT = 2000;
    private static long time;
    private static long recordTag;

    public static boolean check(long tag) {
        long newTime = System.currentTimeMillis();
        if (recordTag != tag) {
            recordTag = tag;
            time = newTime;
            return true;
        }
        boolean result = newTime - time > TIME_OUT;
        if (result) {
            time = newTime;
        }
        return result;
    }


}
