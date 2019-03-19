package com.j1.marblerye;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MarbleUtils {

    private static final String TAG = "MarbleUtils";

    public static String convertLongToDate(long input, String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(input);

        return new SimpleDateFormat(dateFormat, Locale.US).format(calendar.getTime());
    }

    public static long rewindToStartOfWeek(long input) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(input);
        // set to beginning of day
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        while (today != Calendar.SUNDAY) {
            // keep subtracting days until we get to the start of week
            calendar.add(Calendar.DAY_OF_WEEK, -1);
            today = calendar.get(Calendar.DAY_OF_WEEK);
        }
        // once here, calendar should be set to start of week
        return calendar.getTimeInMillis();
    }

    public static long convertDateToLong(Context context, String date) {
        return convertDateToLong(date, context.getString(R.string.date_format_pattern));
    }

    public static long convertDateToLong(String date, String format) {
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

    public static long getTodaysDateInMillis() {
        Calendar calendar = Calendar.getInstance();

        return calendar.getTimeInMillis();
    }

    public static boolean exportDatabaseToCsv(Context context, SQLiteDatabase database) {
        CSVWriter writer = new CSVWriter();
        String header = MarbleDBContract.Expenses.COLUMN_DATE + ","
                + MarbleDBContract.Expenses.COLUMN_DESCRIPTION + ","
                + MarbleDBContract.Expenses.COLUMN_AMOUNT;
        writer.setHeader(header);
        Cursor cursor = database.rawQuery(
                "select * from " + MarbleDBContract.Expenses.TABLE_NAME +
                        " order by " + MarbleDBContract.Expenses.COLUMN_DATE + " desc",
                null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                writer.writeLine(
                    convertLongToDate(cursor.getLong(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE)), "dd MMM yyyy"),
                    cursor.getString(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_AMOUNT))
                );
                writer.nextLine();
                cursor.moveToNext();
            }
        }
        cursor.close();
        // write CSV to file:
        Calendar calendar = Calendar.getInstance();
        String date = new SimpleDateFormat("ddMMMyyy", Locale.US).format(calendar.getTime());
        String fileName = "MarbleRyeDatabaseExport_" + date + ".csv";
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // only proceed if we have write access to storage
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            final File file = new File(path, fileName);
            try {
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.append(writer.getCSV());
                outputStreamWriter.close();
                outputStream.flush();
                outputStream.close();
                Toast.makeText(context, "File created in " + file.getPath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        return true;
    }

}
