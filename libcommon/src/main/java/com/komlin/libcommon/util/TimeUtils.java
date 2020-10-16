package com.komlin.libcommon.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author lipeiyong
 * @date 2019/8/14 9:13
 */
public class TimeUtils {

    private static final String POS_FORMAT = "yyyy年MM月dd日\nHH:mm";

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(POS_FORMAT);
        return sdf.format(new Date());
    }

    public static String getWeekDay(Date date) {
        String week = "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int wek = c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            week += "星期日";
        }
        if (wek == 2) {
            week += "星期一";
        }
        if (wek == 3) {
            week += "星期二";
        }
        if (wek == 4) {
            week += "星期三";
        }
        if (wek == 5) {
            week += "星期四";
        }
        if (wek == 6) {
            week += "星期五";
        }
        if (wek == 7) {
            week += "星期六";
        }
        return week;

    }
}
