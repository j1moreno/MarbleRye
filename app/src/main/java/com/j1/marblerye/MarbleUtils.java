package com.j1.marblerye;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MarbleUtils {

    private static final String TAG = "MarbleUtils";

    public static String convertLongToDate(long input) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(input);

        return new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
    }

    public static long convertDateToLongOrThrow(String date) throws ParseException {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(date));

            return calendar.getTimeInMillis();
        }
        catch (Exception e) {
            Log.e(TAG, "Unable to parse date, it's probably in the incorrect format", e);
            throw e;
        }
    }

    public static long getTodaysDateInMillis() throws ParseException {
        long value = 0;
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        calendar.setTime(dateFormat.parse(dateFormat.format(date)));
        value = calendar.getTimeInMillis();

        return value;
    }

}
