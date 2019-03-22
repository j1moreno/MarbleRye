package com.j1.marblerye;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class DetailedExpenseHistoryActivity extends AppCompatActivity {

    private MarbleRecycleAdapter mAdapter;
    private long dateLower, dateUpper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_history);
        int calendarChunkSize = getIntent().getIntExtra("CALENDAR_CHUNK_SIZE", Calendar.DAY_OF_MONTH);
        String dateToSearch = getIntent().getStringExtra("DATE_TO_SEARCH");
        String titlePrefix = (calendarChunkSize == Calendar.WEEK_OF_YEAR) ? "Week of " : "";
        setTitle(titlePrefix + dateToSearch);
        if (calendarChunkSize == Calendar.MONTH) {
            dateToSearch = "01 " + dateToSearch;
        }
        dateLower = MarbleUtils.convertDateToLong(this, dateToSearch);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateLower);
        calendar.add(calendarChunkSize, 1);
        dateUpper = calendar.getTimeInMillis();
        // display data from database
        database = new MarbleDBHelper(this).getReadableDatabase();

        // create array list with desired data:
        ArrayList<HistoryData> dataset = getDataset();

        // create recycle view and adapter for data
        RecyclerView recyclerView = findViewById(R.id.detailedHistory_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // specify an adapter (see also next example)
        mAdapter = new MarbleRecycleAdapter(dataset, R.layout.database_read_item);
        mAdapter.setOnItemClickListener(new MarbleRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistoryData data) {
                String itemRowId = data.rowId;
                String itemAmount = data.amount.substring(1);   // remove $ from beginning of string
                String itemDescription = data.description;
                long itemDate = MarbleUtils.convertDateToLong(getApplicationContext(), data.date);
                // put fields into intent to pass to edit activity
                Intent intent = new Intent(DetailedExpenseHistoryActivity.this, EditExpenseActivity.class);
                intent.putExtra("ID", itemRowId);
                intent.putExtra("AMOUNT", itemAmount);
                intent.putExtra("DESCRIPTION", itemDescription);
                intent.putExtra("DATE", itemDate);
                startActivity(intent);
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
    }

    public void onResume() {
        ArrayList<HistoryData> dataset = getDataset();
        mAdapter.setData(dataset);
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private ArrayList<HistoryData> getDataset() {
        Cursor cursor = database.rawQuery(
                "select * from " + MarbleDBContract.Expenses.TABLE_NAME +
                        " where " + MarbleDBContract.Expenses.COLUMN_DATE + " >= " + dateLower +
                        " and " + MarbleDBContract.Expenses.COLUMN_DATE + " < " + dateUpper +
                        " order by " + MarbleDBContract.Expenses.COLUMN_DATE + " desc",
                null);

        // create array list with desired data:
        ArrayList<HistoryData> dataset = new ArrayList<HistoryData>();
        HistoryData data;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                data = new HistoryData();
                data.rowId = cursor.getString(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses._ID));
                data.description = cursor.getString(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DESCRIPTION));
                data.date = MarbleUtils.convertLongToDate(cursor.getLong(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE)), "dd MMM yyyy");
                data.amount = getString(R.string.display_amount, Double.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_AMOUNT))));
                dataset.add(data);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return dataset;
    }
}
