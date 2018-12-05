package com.j1.marblerye;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MarbleUtils {

    private static final String TAG = "MarbleUtils";

    public static boolean isDateInCorrectFormat(String date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(date));
        }
        catch (Exception e) {
            return false;
        }
        return true;
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

}
