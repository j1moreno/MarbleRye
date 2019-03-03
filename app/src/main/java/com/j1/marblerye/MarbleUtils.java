package com.j1.marblerye;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MarbleUtils {

    private static final String TAG = "MarbleUtils";

    public static String convertLongToDate(long input, String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(input);

        return new SimpleDateFormat(dateFormat, Locale.US).format(calendar.getTime());
    }

    public static long convertDateToLong(Context context, String date) {
        return convertDateToLong(context, date, context.getString(R.string.date_format_pattern));
    }

    public static long convertDateToLong(Context context, String date, String format) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat(format, Locale.US)).parse(date));

            return calendar.getTimeInMillis();
        }
        catch (Exception e) {
            Log.e(TAG, "Unable to parse date, it's probably in the incorrect format", e);
            return 0;
        }
    }

    public static long getTodaysDateInMillis(Context context) throws ParseException {
        long value = 0;
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.date_format_pattern), Locale.US);
        calendar.setTime(dateFormat.parse(dateFormat.format(date)));
        value = calendar.getTimeInMillis();

        return value;
    }

}
