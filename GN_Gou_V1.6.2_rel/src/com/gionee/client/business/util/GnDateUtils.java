package com.gionee.client.business.util;

import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.gionee.client.R;

public class GnDateUtils {

    private static final int MORE_THAN_FOUR_DAYS = 5;

    public static String formatDuring(Context context, long current, long lastupdate) {
//        String time = "";
//        long days = getAbsIntervalDats(current, lastupdate);
//        if (days == 0) {
//            time = timeStamp2HHMM(lastupdate);
//        } else if (1 == days) {
//            time = context.getResources().getString(R.string.yesterday) + "  " + timeStamp2HHMM(lastupdate);
//        } else if (days > 1 && days <= 6) {
//            time = getWeek(context, lastupdate) + "  " + ;
//        } else {
//            time = timeStamp2Date(lastupdate);
//        }
        return timeStamp2HHMM(lastupdate);
    }

    public static String formatDays(Context context, long days, long lastMillis) {
        if (days == 0) {
            return context.getString(R.string.today);
        } else if (1 == days) {
            return context.getString(R.string.yesterday);
        } else if (days > 1 && days <= 6) {
            return getWeek(context, lastMillis);
        } else {
            return context.getString(R.string.one_week_ago);
        }
    }

    public static long getAbsIntervalDats(long current, long lastupdate) {
        long days = Math.abs(getIntervalDay(current, lastupdate));
        return days;
    }

    public static String timeStamp2HHMM(long timestamp) {
        String time = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(timestamp));
        return time;
    }

    public static String timeStamp2Date(long timestamp) {
        String date = new java.text.SimpleDateFormat("MM-dd  HH:mm").format(new java.util.Date(timestamp));
        return date;
    }

    public static int getIntervalDay(long current, long lastupdate) {

        int intervalDay = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(current);
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int cureentDate = cal.get(Calendar.DATE);

        cal.setTimeInMillis(lastupdate);

        int lastYear = cal.get(Calendar.YEAR);
        int lastMoth = cal.get(Calendar.MONTH) + 1;
        int lastDate = cal.get(Calendar.DATE);

        if (currentYear != lastYear) {
            intervalDay = MORE_THAN_FOUR_DAYS;
        } else if (currentMonth != lastMoth) {
            intervalDay = getIntervalDay(currentMonth, cureentDate, lastMoth, lastDate);
        } else {
            intervalDay = cureentDate - lastDate;
        }
        return intervalDay;
    }

    public static int getIntervalDay(int currentMonth, int cureentDate, int lastMoth, int lastDate) {
        int intervalDay = 0;
        int interalMoth = currentMonth - lastMoth;
        if (Math.abs(interalMoth) > 1) {
            intervalDay = MORE_THAN_FOUR_DAYS;
        } else if (currentMonth < lastMoth) {
            intervalDay = getDay(currentMonth, cureentDate, lastDate);
        } else {
            intervalDay = getDay(lastMoth, lastDate, cureentDate);
        }

        return intervalDay;
    }

    public static int getDay(int currentMonth, int cureentDate, int lastDate) {
        int intervalDay = 0;

        if (currentMonth == 1 || currentMonth == 3 || currentMonth == 5 || currentMonth == 7
                || currentMonth == 8 || currentMonth == 10 || currentMonth == 12) {
            intervalDay = 31 - cureentDate + lastDate;
        } else if (currentMonth == 2) {
            intervalDay = 28 - cureentDate + lastDate;
        } else {
            intervalDay = 30 - cureentDate + lastDate;
        }

        return intervalDay;
    }

    public static String getWeek(Context context, long timestamp) {

        String week = "";
        Resources resources = context.getResources();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        int weekIndex = cal.get(Calendar.DAY_OF_WEEK);
        if (weekIndex <= 0) {
            return week;
        }
        switch (weekIndex) {
            case 1:
                week = resources.getString(R.string.sunday);
                break;
            case 2:
                week = resources.getString(R.string.monday);
                break;
            case 3:
                week = resources.getString(R.string.tuesday);
                break;
            case 4:
                week = resources.getString(R.string.wednesday);
                break;
            case 5:
                week = resources.getString(R.string.thursday);
                break;
            case 6:
                week = resources.getString(R.string.friday);
                break;
            case 7:
                week = resources.getString(R.string.saturday);
                break;
            default:
                break;
        }
        return week;
    }

    public static long string2Long(String timeString) {
        long result = -1;
        if (TextUtils.isEmpty(timeString)) {
            return result;
        }
        try {
            return Long.parseLong(timeString);
        } catch (NumberFormatException e) {
        }
        return result;
    }
}
