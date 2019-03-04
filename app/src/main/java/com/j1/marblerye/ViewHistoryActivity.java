package com.j1.marblerye;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DecimalFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewHistoryActivity extends AppCompatActivity {

    private int calendarChunkSize;
    private String dateFormat;
    private SQLiteDatabase database;
    private MarbleRecycleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);
        Intent intent = getIntent();
        calendarChunkSize = intent.getIntExtra("CALENDAR_CHUNK_SIZE", Calendar.DAY_OF_MONTH);
        dateFormat = intent.getStringExtra("DATE_FORMAT");
        // set title based on what action started the activity
        String title = (calendarChunkSize == Calendar.DAY_OF_MONTH) ? "Daily" : "Monthly";
        title += " History";
        setTitle(title);
        database = new MarbleDBHelper(this).getReadableDatabase();
        // create array list with desired data:
        ArrayList<HistoryData> dataset = getDataset();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // specify an adapter (see also next example)
        mAdapter = new MarbleRecycleAdapter(dataset, R.layout.history_item_single_line);
        mAdapter.setOnItemClickListener(new MarbleRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistoryData data) {
                Intent intent = new Intent(ViewHistoryActivity.this, DetailedExpenseHistoryActivity.class);
                String date = data.date;
                Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
                intent.putExtra("DATE_TO_SEARCH", date);
                intent.putExtra("DATE_FORMAT", dateFormat);
                intent.putExtra("CALENDAR_CHUNK_SIZE", calendarChunkSize);
                startActivity(intent);

            }
        });
        recyclerView.setAdapter(mAdapter);

    }

    public void onResume() {
        ArrayList<HistoryData> dataset = getDataset();
        mAdapter.setData(dataset);
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private ArrayList<HistoryData> getDataset() {
        ArrayList<HistoryData> dataset = new ArrayList<HistoryData>();
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
                        data.date = MarbleUtils.convertLongToDate(currentDate, dateFormat);
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
            data.date = MarbleUtils.convertLongToDate(currentDate, dateFormat);
            data.amount = getString(R.string.display_amount, tempAmount);
            dataset.add(data);
        }
        cursor.close();

        return dataset;
    }
}
