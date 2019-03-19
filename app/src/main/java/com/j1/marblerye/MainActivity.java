package com.j1.marblerye;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Fab button click will start new expense activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewExpenseInput.class);
                startActivity(intent);
            }
        });
        // set listener for history buttons:
        Button buttonHistoryDaily = findViewById(R.id.see_daily_history);
        buttonHistoryDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewHistoryActivity.class);
                intent.putExtra("CALENDAR_CHUNK_SIZE", Calendar.DAY_OF_MONTH);
                intent.putExtra("DATE_FORMAT", getString(R.string.date_format_pattern));
                startActivity(intent);
            }
        });
        Button buttonHistoryWeekly = findViewById(R.id.see_weekly_history);
        buttonHistoryWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewHistoryActivity.class);
                intent.putExtra("CALENDAR_CHUNK_SIZE", Calendar.WEEK_OF_YEAR);
                intent.putExtra("DATE_FORMAT", getString(R.string.date_format_pattern));
                startActivity(intent);
            }
        });
        Button buttonHistoryMonthly = findViewById(R.id.see_monthly_history);
        buttonHistoryMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewHistoryActivity.class);
                intent.putExtra("CALENDAR_CHUNK_SIZE", Calendar.MONTH);
                intent.putExtra("DATE_FORMAT", getString(R.string.date_format_pattern_month));
                startActivity(intent);
            }
        });
        // calculate averages and display values
        calculateDataAndDisplay();
    }

    public void onResume() {
        calculateDataAndDisplay();
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
        if (id == R.id.action_exportCsv) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
            return true;
        } else if (id == R.id.see_all_history) {
            Intent intent = new Intent(MainActivity.this, ReadDBActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            MarbleUtils.exportDatabaseToCsv(this, database);
        }
    }

    private void calculateDataAndDisplay() {
        database = new MarbleDBHelper(this).getReadableDatabase();
        // daily:
        TextView spendToday = findViewById(R.id.textViewSpendToday);
        spendToday.setText(getString(R.string.display_amount, MarbleCalculator.getTodaySpending(database)));
        TextView spendDailyAverage = findViewById(R.id.textViewSpendAvg);
        spendDailyAverage.setText(getString(R.string.display_amount, MarbleCalculator.getAverageDailySpending(database)));
        // weekly:
        TextView spendThisWeek = findViewById(R.id.textView_main_currentWeekAmt);
        spendThisWeek.setText(getString(R.string.display_amount, MarbleCalculator.getSpendingThisWeek(database)));
        TextView spendAverageWeekly = findViewById(R.id.textView_main_avgWeekAmt);
        spendAverageWeekly.setText(getString(R.string.display_amount, MarbleCalculator.getAverageWeeklySpending(database)));
        // monthly:
        TextView spendCurrentMonth = findViewById(R.id.textView_main_currentMonthAmt);
        spendCurrentMonth.setText(getString(R.string.display_amount, MarbleCalculator.getCurrentMonthSpending(database)));
        TextView spendAverageMonthly = findViewById(R.id.textView_main_avgMonthAmt);
        spendAverageMonthly.setText(getString(R.string.display_amount, MarbleCalculator.getAverageMonthlySpending(database)));
    }
}
