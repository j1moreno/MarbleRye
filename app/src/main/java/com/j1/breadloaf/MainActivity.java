package com.j1.breadloaf;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewExpenseInput.class);
                startActivity(intent);
            }
        });

        TextView spendToday = findViewById(R.id.textViewSpendToday);
        spendToday.setText("$" + String.valueOf(getTodaySpending()));

        Button buttonReadDB = findViewById(R.id.buttonReadDB);
        buttonReadDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReadDBActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onResume() {
        TextView spendToday = findViewById(R.id.textViewSpendToday);
        spendToday.setText("$" + String.valueOf(getTodaySpending()));
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private long getTodaysDateInMillis() throws ParseException {
        long value = 0;
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        calendar.setTime(dateFormat.parse(dateFormat.format(date)));
        value = calendar.getTimeInMillis();

        return value;
    }

    private double getTodaySpending() {
        // query db for all values entered today
        SQLiteDatabase database = new BreadLoafDBHelper(this).getReadableDatabase();
        long date = 0;
        try {
            date = getTodaysDateInMillis();
        } catch (Exception e) {
            Log.d(TAG, "exception caught!" + e.toString());
        }
        String[] projection = {};

        String selection =  BreadLoafDBContract.Expenses.COLUMN_DATE + " == ?";

        String[] selectionArgs = {date + ""};

        Cursor cursor = database.query(
                BreadLoafDBContract.Expenses.TABLE_NAME,     // The table to query
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
}
