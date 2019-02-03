package com.j1.marblerye;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MarbleCalculator {

    private static final String TAG = "MarbleCalculator";
    private static final long MILLISECONDS_IN_A_DAY = 86400000;
    private static final int DAYS_IN_A_MONTH = 30;

    public static double getAverageMonthlySpending(Context context, SQLiteDatabase database) {
        double average;
        double total = 0.00;
        long days;
        long currentDate = 0;
        long firstDate = 0;
        boolean firstEntry = true;
        // init database
        database = new MarbleDBHelper(context).getReadableDatabase();
        // query for all entries
        Cursor cursor = database.rawQuery("select * from "+MarbleDBContract.Expenses.TABLE_NAME,null);
        // read data retrieved from DB
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                double value = Double.valueOf(cursor.getString(2));
                if (firstEntry) {
                    // if first entry contains first date
                    firstDate = cursor.getLong(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE));
                    firstEntry = false; // set flag, no longer first entry
                }
                total += value; // add up every value in DB
                cursor.moveToNext();
            }
        }
        cursor.close();
        try {
            currentDate = MarbleUtils.getTodaysDateInMillis();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        // days is currentDate - firstDate, divided by MS in a day; add one to include today
        days = (long) ((currentDate-firstDate)/(MILLISECONDS_IN_A_DAY * DAYS_IN_A_MONTH)) + 1;    // include today
        Log.d(TAG, "currentDate:  " + currentDate);
        Log.d(TAG, "firstDate:  " + firstDate);
        Log.d(TAG, "days:  " + days);
        Log.d(TAG, "difference: " + (currentDate-firstDate));
        average = total/days;

        return average;
    }

}
