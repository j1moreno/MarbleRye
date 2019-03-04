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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);
        int calendarChunkSize = getIntent().getIntExtra("CALENDAR_CHUNK_SIZE", Calendar.DAY_OF_MONTH);
        String dateToSearch = getIntent().getStringExtra("DATE_TO_SEARCH");
        setTitle(dateToSearch);
        if (calendarChunkSize == Calendar.MONTH) {
            dateToSearch = "01 " + dateToSearch;
        }
        long dateLower = MarbleUtils.convertDateToLong(this, dateToSearch);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateLower);
        calendar.add(calendarChunkSize, 1);
        long dateUpper = calendar.getTimeInMillis();
        Log.d("Detail", ""+dateLower);
        // display data from database
        SQLiteDatabase database = new MarbleDBHelper(this).getReadableDatabase();
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

        // create recycle view and adapter for data
        RecyclerView recyclerView = findViewById(R.id.recycler_view_history);
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
        // @todo: update items in list
        super.onResume();
    }
}
