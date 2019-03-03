package com.j1.marblerye;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;

public class DetailedExpenseHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);
        long dateToSearch = MarbleUtils.convertDateToLong(
                this,
                getIntent().getStringExtra("DATE_TO_SEARCH"));
        int calendarChunkSize = getIntent().getIntExtra("CALENDAR_CHUNK_SIZE", Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateToSearch);
        calendar.add(calendarChunkSize, 1);
        long dateUpper = calendar.getTimeInMillis();
        Log.d("Detail", ""+dateToSearch);
        // display data from database
        SQLiteDatabase database = new MarbleDBHelper(this).getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "select * from " + MarbleDBContract.Expenses.TABLE_NAME +
                        " where " + MarbleDBContract.Expenses.COLUMN_DATE + " >= " + dateToSearch +
                        " and " + MarbleDBContract.Expenses.COLUMN_DATE + " < " + dateUpper +
                        " order by " + MarbleDBContract.Expenses.COLUMN_DATE + " desc",
                null);

        // create array list with desired data:
        ArrayList<HistoryData> dataset = new ArrayList<HistoryData>();
        HistoryData data;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                data = new HistoryData();
                data.description = cursor.getString(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DESCRIPTION));
                data.date = MarbleUtils.convertLongToDate(cursor.getLong(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE)), "dd MMM yyyy");
                data.amount = getString(R.string.display_amount, Double.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_AMOUNT))));
                dataset.add(data);
                cursor.moveToNext();
            }
        }
        cursor.close();

        // create recycle view and adapter for data
        RecyclerView recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // specify an adapter (see also next example)
        MarbleRecycleAdapter mAdapter = new MarbleRecycleAdapter(dataset, R.layout.database_read_item);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
    }
}
