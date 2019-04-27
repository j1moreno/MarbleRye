package com.j1.marblerye;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ViewHistoryActivity extends AppCompatActivity {

    private int calendarChunkSize;
    private String dateFormat;
    private SQLiteDatabase database;
    private MarbleRecycleAdapter mAdapter;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);
        Intent intent = getIntent();
        calendarChunkSize = intent.getIntExtra("CALENDAR_CHUNK_SIZE", Calendar.DAY_OF_MONTH);
        dateFormat = intent.getStringExtra("DATE_FORMAT");
        // set title based on what action started the activity
        String title = "";
        String spendingPerChunk = "Spending Per ";
        if (calendarChunkSize == Calendar.MONTH) {
            title += "Monthly";
            spendingPerChunk += "Month";
        } else if (calendarChunkSize == Calendar.WEEK_OF_YEAR) {
            title += "Weekly";
            spendingPerChunk += "Week";
        } else {
            title += "Daily";
            spendingPerChunk += "Day";
        }
        title += " History";
        setTitle(title);
        TextView textView_spendingPerChunk = findViewById(R.id.viewHistory_textView_spendingPerChunk);
        textView_spendingPerChunk.setText(spendingPerChunk);
        database = new MarbleDBHelper(this).getReadableDatabase();
        // create array list with desired data:
        ArrayList<HistoryData> dataset = getDataset();

        RecyclerView recyclerView = findViewById(R.id.viewHistory_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // specify an adapter (see also next example)
        mAdapter = new MarbleRecycleAdapter(dataset, R.layout.history_item_single_line);
        mAdapter.setOnItemClickListener(new MarbleRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistoryData data) {
                Intent intent = new Intent(ViewHistoryActivity.this, DetailedExpenseHistoryActivity.class);
                String date = data.date;
                intent.putExtra("DATE_TO_SEARCH", date);
                intent.putExtra("DATE_FORMAT", dateFormat);
                intent.putExtra("CALENDAR_CHUNK_SIZE", calendarChunkSize);
                startActivity(intent);

            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // in this example, a LineChart is initialized from xml
        chart = findViewById(R.id.chart);

        ArrayList<HistoryData> reversedDataSet = new ArrayList<>(dataset);
        Collections.reverse(reversedDataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineWidth(1f);
        xAxis.setAxisLineColor(Color.BLACK);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisLineWidth(1f);
        leftAxis.setAxisLineColor(Color.BLACK);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setText("");

        chart.setData(getGraphLineData(reversedDataSet));
        chart.setDrawGridBackground(false);
        chart.invalidate(); // refresh

        chart.getParent().requestChildFocus(chart, chart);

    }

    public void onResume() {
        ArrayList<HistoryData> dataset = getDataset();
        mAdapter.setData(dataset);
        mAdapter.notifyDataSetChanged();
        ArrayList<HistoryData> reversedDataSet = new ArrayList<>(dataset);
        Collections.reverse(reversedDataSet);
        chart.setData(getGraphLineData(reversedDataSet));
        chart.invalidate();
        super.onResume();
    }

    private ArrayList<HistoryData> getDataset() {
        ArrayList<HistoryData> dataset = new ArrayList<>();
        // get data from database
        Cursor cursor = database.rawQuery(
                "select * from " + MarbleDBContract.Expenses.TABLE_NAME +
                        " order by " + MarbleDBContract.Expenses.COLUMN_DATE + " desc",
                null);
        Calendar calendar = Calendar.getInstance();
        int tempDay = 0;
        long date;
        long currentDate = 0;
        Double tempAmount = 0.00;
        HistoryData data;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                date = cursor.getLong(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE));
                calendar.setTimeInMillis(date);
                if (tempDay != calendar.get(calendarChunkSize)) {
                    if (currentDate != 0) {
                        data = new HistoryData();
                        // special case for weekly spending:
                        if (calendarChunkSize == Calendar.WEEK_OF_YEAR) {
                            currentDate = MarbleUtils.rewindToStartOfWeek(currentDate);
                        }
                        data.date = MarbleUtils.convertLongToDate(currentDate, dateFormat);
                        data.longDate = currentDate;
                        data.amount = getString(R.string.display_amount, tempAmount);
                        dataset.add(data);
                    }
                    tempDay = calendar.get(calendarChunkSize);
                    currentDate = date;
                    tempAmount = 0.00;
                }
                double value = Double.valueOf(cursor.getString(2));
                tempAmount += value;


                cursor.moveToNext();
            }
            // add the last values after loop is done
            data = new HistoryData();
            // special case for weekly spending:
            if (calendarChunkSize == Calendar.WEEK_OF_YEAR) {
                currentDate = MarbleUtils.rewindToStartOfWeek(currentDate);
            }
            data.date = MarbleUtils.convertLongToDate(currentDate, dateFormat);
            data.longDate = currentDate;
            data.amount = getString(R.string.display_amount, tempAmount);
            dataset.add(data);
        }
        cursor.close();

        return dataset;
    }

    private float getXAxisIncrement(long previous, long current) {
        Calendar calendar = Calendar.getInstance();
        float chunkCounter = 0;
        Log.d("getXAxisincrement - starting dates: ", MarbleUtils.convertLongToDate(previous, "ddMMMyyyy") +
                " " + MarbleUtils.convertLongToDate(current, "ddMMMyyyy"));
        int incrementedValue;
        while (previous < current) {
            chunkCounter++;
            calendar.setTimeInMillis(previous);
            calendar.add(calendarChunkSize, 1);
            incrementedValue = calendar.get(calendarChunkSize);
            previous = calendar.getTimeInMillis();
            calendar.setTimeInMillis(current);
            if (calendar.get(calendarChunkSize) == incrementedValue) {
                break;
            }
            Log.d("getXAxisincrement - date now: ", MarbleUtils.convertLongToDate(previous, "ddMMMyyyy"));
        }

        return chunkCounter;
    }

    private LineData getGraphLineData(ArrayList<HistoryData> dataSet) {
        float xCount = 0;
        float xIncrement;
        long previousDate = 0;
        List<Entry> entries = new ArrayList<>();
        for (HistoryData data : dataSet) {
            if (previousDate > 0){
                xIncrement = getXAxisIncrement(previousDate, data.longDate);
                if (xIncrement > 1) {
                    for (int i = 1; i < xIncrement; i++) {
                        entries.add(new Entry(xCount+i, 0));
                    }
                }
                xCount += xIncrement;
            }
            previousDate = data.longDate;
            entries.add(new Entry(xCount, Float.valueOf(data.amount.substring(1))));
            Log.d("graphTest - xCount", String.valueOf(xCount));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, ""); // add entries to dataset
        lineDataSet.setColor(Color.MAGENTA);
        lineDataSet.setCircleColor(Color.MAGENTA);
        lineDataSet.setCircleRadius(1f);
        lineDataSet.setValueTextColor(Color.BLUE);
        lineDataSet.setHighLightColor(Color.BLUE);

        return new LineData(lineDataSet);
    }
}
