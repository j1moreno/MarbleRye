package com.j1.marblerye;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MarbleCalculator {

    private static final String TAG = "MarbleCalculator";
    private static final long MILLISECONDS_IN_A_DAY = 86400000;
    private static final int DAYS_IN_A_MONTH = 30;

    public static double getAverageMonthlySpending(SQLiteDatabase database) {
        ArrayList months = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        long date;
        int previousMonth = 0;
        int currentMonth;
        double average;
        double total = 0.00;
        int monthCount = 0;
        // init database
        // query for all entries
        Cursor cursor = database.rawQuery(
                "select * from " + MarbleDBContract.Expenses.TABLE_NAME +
                        " order by " + MarbleDBContract.Expenses.COLUMN_DATE + " asc",
                null);
        // read data retrieved from DB
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // get amount field from entry
                double amount = Double.valueOf(cursor.getString(2));
                // get date field, in long format
                date = cursor.getLong(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE));
                // convert long date to calendar format
                calendar.setTimeInMillis(date);
                // get month from calendar
                currentMonth = calendar.get(Calendar.MONTH);
                Log.d(TAG, "previousMonth: " + previousMonth);
                Log.d(TAG, "currentMonth: " + currentMonth);
                if (monthCount == 0) {
                    // no months have been counted yet, just add 1
                    monthCount += 1;
                } else {
                    // if current month has changed from previous entry,
                    // set calendar to match previous month
                    if (previousMonth != currentMonth) calendar.set(Calendar.MONTH, previousMonth);
                    // increment until they match
                    while (previousMonth != currentMonth) {
                        // increment previous month by 1
                        calendar.add(Calendar.MONTH, 1);
                        previousMonth = calendar.get(Calendar.MONTH);
                        // increment number of months
                        monthCount += 1;
                    }
                }
                // set previousMonth for next pass
                previousMonth = currentMonth;
                total += amount; // add up every value in DB
                cursor.moveToNext();
                // log values
                Log.d(TAG, "monthCount: " + monthCount);
            }
        }
        cursor.close();
        // Don't divide by 0, return 0 instead
        if (monthCount <= 0) {
            return 0.00;
        } else {
            average = total/monthCount;
            return average;
        }
    }

    public static double getCurrentMonthSpending(SQLiteDatabase database) {
        int tempMonth;
        double total = 0.00;
        long date;
        double tempValue;
        // get current month
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
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

    public static double getSpendingThisWeek(SQLiteDatabase database) {
        double spentThisWeek = 0;
        // figure out previous monday and start dates from there
        Calendar calendar = Calendar.getInstance();
        // set to beginning of day
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        while (today != Calendar.SUNDAY) {
            // keep subtracting days until we get to the previous monday
            calendar.add(Calendar.DAY_OF_WEEK, -1);
            today = calendar.get(Calendar.DAY_OF_WEEK);
        }
        // once here, calendar should be set to Monday
        long date = calendar.getTimeInMillis();
        // search database for any date on or after last Monday
        String selection =  MarbleDBContract.Expenses.COLUMN_DATE + " >= ?";

        String[] selectionArgs = {date + ""};

        Cursor cursor = database.query(
                MarbleDBContract.Expenses.TABLE_NAME,     // The table to query
                null,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                double value = Double.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_AMOUNT)));
                Log.d(TAG, "value:  " + value);
                spentThisWeek += value;
                cursor.moveToNext();
            }
        }
        cursor.close();

        return spentThisWeek;
    }

    public static double getAverageWeeklySpending(SQLiteDatabase database) {
        ArrayList weeks = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        long date;
        int tempWeek;
        double average;
        double total = 0.00;
        // init database
        // query for all entries
        Cursor cursor = database.rawQuery("select * from "+MarbleDBContract.Expenses.TABLE_NAME,null);
        // read data retrieved from DB
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                double value = Double.valueOf(cursor.getString(2));
                date = cursor.getLong(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE));
                calendar.setTimeInMillis(date);
                tempWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                if (!weeks.contains(tempWeek)) {
                    weeks.add(tempWeek);
                }
                total += value; // add up every value in DB
                cursor.moveToNext();
            }
        }
        cursor.close();
        // Don't divide by 0, return 0 instead
        if (weeks.size() <= 0) {
            return 0.00;
        } else {
            average = total/weeks.size();
            return average;
        }
    }

    public static double getTodaySpending(SQLiteDatabase database) {
        long date = 0;
        Calendar calendar = Calendar.getInstance();
        Log.d(TAG, "getTodaysSpending - date before mod: " + calendar.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        try {
            date = calendar.getTimeInMillis();
            Log.d(TAG, "getTodaysSpending - today's date: " + date);
        } catch (Exception e) {
            Log.d(TAG, "exception caught!" + e.toString());
        }
        String[] projection = {};

        String selection =  MarbleDBContract.Expenses.COLUMN_DATE + " >= ?";

        String[] selectionArgs = {date + ""};

        Cursor cursor = database.query(
                MarbleDBContract.Expenses.TABLE_NAME,     // The table to query
                null,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );
        Log.d(TAG, "The total cursor count is " + cursor.getCount());

        double total = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                double value = Double.valueOf(cursor.getString(2));
                Log.d(TAG, "value:  " + value);
                total += value;
                cursor.moveToNext();
            }
        }
        cursor.close();
        Log.d(TAG, "total:  " + total);

        return total;
    }

    public static double getAverageDailySpending(SQLiteDatabase database) {
        double average;
        double total = 0.00;
        int days;
        long currentDate = 0;
        long firstDate = 0;
        boolean firstEntry = true;
        // query for all entries
        Cursor cursor = database.rawQuery(
                "select * from " + MarbleDBContract.Expenses.TABLE_NAME +
                        " order by " + MarbleDBContract.Expenses.COLUMN_DATE + " asc",
                null);
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
        days = (int) ((currentDate-firstDate)/MILLISECONDS_IN_A_DAY) + 1;    // include today
        Log.d(TAG, "currentDate:  " + currentDate);
        Log.d(TAG, "firstDate:  " + firstDate);
        Log.d(TAG, "days:  " + days);
        Log.d(TAG, "difference: " + (currentDate-firstDate));
        average = total/days;

        return average;
    }

    public static String [] getMostUsedDescriptions(SQLiteDatabase database, int nthMax) {
        HashMap descriptionFrequencies = new HashMap();
        Cursor cursor = database.rawQuery("select * from "+MarbleDBContract.Expenses.TABLE_NAME,null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String description = cursor.getString(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DESCRIPTION));
                if (descriptionFrequencies.containsKey(description)) {
                    descriptionFrequencies.put(description, (int)descriptionFrequencies.get(description)+1);
                } else {
                    descriptionFrequencies.put(description, 1);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();

        String[] maxValues = new String[nthMax];
        LinkedHashMap sortedValues = sortHashMapByValues(descriptionFrequencies);

        // Get a set of the entries
        Set set = sortedValues.entrySet();
        // Get an iterator
        Iterator iterator = set.iterator();
        // Display elements
        int i = 0;
        while (iterator.hasNext() && (i < nthMax)) {
            Map.Entry entry = (Map.Entry)iterator.next();
            maxValues[i] = (String) entry.getKey();
            i++;
        }

        return maxValues;
    }

    public static LinkedHashMap<String, Integer> sortHashMapByValues(
            HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues, Collections.<Integer>reverseOrder());

        LinkedHashMap<String, Integer> sortedMap =
                new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            int val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                int comp1 = passedMap.get(key);
                int comp2 = val;

                if (comp1 == comp2) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

}
