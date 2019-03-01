package com.j1.marblerye;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewHistoryActivity extends AppCompatActivity {

    private int calendarChunkSize;
    private String dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);
        Intent intent = getIntent();
        calendarChunkSize = intent.getIntExtra("CALENDAR_CHUNK_SIZE", Calendar.DAY_OF_MONTH);
        dateFormat = intent.getStringExtra("DATE_FORMAT");
        // display data from database
        SQLiteDatabase database = new MarbleDBHelper(this).getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "select * from " + MarbleDBContract.Expenses.TABLE_NAME +
                        " order by " + MarbleDBContract.Expenses.COLUMN_DATE + " desc",
                null);

        // create array list with desired data:
        ArrayList<HistoryData> dataset = new ArrayList<HistoryData>();
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

        RecyclerView recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // specify an adapter (see also next example)
        RecycleRowData testData = new RecycleRowData() {
            @Override
            public void bindViews(View v) {
                description = v.findViewById(R.id.history_description);
                amount = v.findViewById(R.id.history_amount);
            }

            @Override
            public void bindData(HistoryData historyData) {
                description.setText(historyData.date);
                amount.setText(historyData.amount);
            }
        };
        MarbleRecycleAdapter mAdapter = new MarbleRecycleAdapter(dataset, R.layout.history_item_single_line, testData);
        mAdapter.setOnItemClickListener(new MarbleRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistoryData data) {
                Toast.makeText(getApplicationContext(), "Amt: ", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(mAdapter);

    }
}
