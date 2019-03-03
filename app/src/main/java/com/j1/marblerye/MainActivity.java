package com.j1.marblerye;

import android.content.Intent;
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
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewExpenseInput.class);
                startActivity(intent);
            }
        });
        calculateDataAndDisplay();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.see_all_history) {
            Intent intent = new Intent(MainActivity.this, ReadDBActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void calculateDataAndDisplay() {
        TextView spendToday = findViewById(R.id.textViewSpendToday);
        spendToday.setText(getString(R.string.display_amount, MarbleCalculator.getTodaySpending(this)));
        TextView spendDailyAverage = findViewById(R.id.textViewSpendAvg);
        spendDailyAverage.setText(getString(R.string.display_amount, MarbleCalculator.getAverageDailySpending(this)));
        TextView spendCurrentMonth = findViewById(R.id.textView_main_currentMonthAmt);
        spendCurrentMonth.setText(getString(R.string.display_amount, MarbleCalculator.getCurrentMonthSpending(this)));
        TextView spendAverageMonthly = findViewById(R.id.textView_main_avgMonthAmt);
        spendAverageMonthly.setText(getString(R.string.display_amount, MarbleCalculator.getAverageMonthlySpending(this)));
    }
}
