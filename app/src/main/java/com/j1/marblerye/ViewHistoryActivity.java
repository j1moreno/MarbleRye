package com.j1.marblerye;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewHistoryActivity extends AppCompatActivity {

    private int calendarChunkSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);
        calendarChunkSize = getIntent().getIntExtra("CALENDAR_CHUNK_SIZE", Calendar.DAY_OF_MONTH);
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
                        data = new HistoryData(MarbleUtils.convertLongToDate(this, currentDate),
                                getString(R.string.display_amount, tempAmount));
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
            data = new HistoryData(MarbleUtils.convertLongToDate(this, currentDate),
                    getString(R.string.display_amount, tempAmount));
            dataset.add(data);
        }
        cursor.close();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // specify an adapter (see also next example)
        MarbleRecycleAdapter mAdapter = new MarbleRecycleAdapter(dataset, R.layout.history_item_single_line);
        recyclerView.setAdapter(mAdapter);

    }
}
