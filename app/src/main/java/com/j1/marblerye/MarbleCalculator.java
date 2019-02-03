package com.j1.marblerye;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class MarbleCalculator {

    private static final String TAG = "MarbleCalculator";
    private static final long MILLISECONDS_IN_A_DAY = 86400000;
    private static final int DAYS_IN_A_MONTH = 30;

    public static double getAverageMonthlySpending(Context context) {
        ArrayList months = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        long date;
        int tempMonth;
        double average;
        double total = 0.00;
        // init database
        SQLiteDatabase database = new MarbleDBHelper(context).getReadableDatabase();
        // query for all entries
        Cursor cursor = database.rawQuery("select * from "+MarbleDBContract.Expenses.TABLE_NAME,null);
        // read data retrieved from DB
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                double value = Double.valueOf(cursor.getString(2));
                date = cursor.getLong(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE));
                calendar.setTimeInMillis(date);
                tempMonth = calendar.get(Calendar.MONTH);
                if (!months.contains(tempMonth)) {
                    months.add(tempMonth);
                }
                total += value; // add up every value in DB
                cursor.moveToNext();
            }
        }
        cursor.close();
        average = total/months.size();

        return average;
    }

    public static double getCurrentMonthSpending(Context context) {
        int tempMonth;
        double total = 0.00;
        long date;
        double tempValue;
        // get current month
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        SQLiteDatabase database = new MarbleDBHelper(context).getReadableDatabase();
        // query for all entries
        Cursor cursor = database.rawQuery("select * from "+MarbleDBContract.Expenses.TABLE_NAME,null);
        // read data retrieved from DB
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // get date from entry
                date = cursor.getLong(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE));
                calendar.setTimeInMillis(date);
                tempMonth = calendar.get(Calendar.MONTH);
                if (tempMonth == currentMonth) {
                    // only add to total if entry matches current month
                    tempValue = Double.valueOf(cursor.getString(2));
                    total += tempValue; // add up every value in DB
                }
                cursor.moveToNext();
            }
        }
        cursor.close();

        return total;
    }

}
