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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);
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
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                date = cursor.getLong(cursor.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE));
                calendar.setTimeInMillis(date);
                if (tempDay != calendar.get(Calendar.DAY_OF_MONTH)) {
                    if (currentDate != 0) {
                        HistoryData data = new HistoryData(MarbleUtils.convertLongToDate(this, currentDate), tempAmount.toString());
                        dataset.add(data);
                    }
                    tempDay = calendar.get(Calendar.DAY_OF_MONTH);
                    currentDate = date;
                    tempAmount = 0.00;
                }
                double value = Double.valueOf(cursor.getString(2));
                tempAmount += value;


                cursor.moveToNext();
            }
        }
        cursor.close();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // specify an adapter (see also next example)
        MarbleRecycleAdapter mAdapter = new MarbleRecycleAdapter(dataset);
        recyclerView.setAdapter(mAdapter);

    }
}
