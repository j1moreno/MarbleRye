package com.j1.marblerye;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReadDBActivity extends AppCompatActivity {

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_db);
        readFromDB();   // show all entries as soon as activity is started
        Button getData = findViewById(R.id.button2);
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFromDB();
            }
        });
        final ListView listView = findViewById(R.id.listViewDB);
        // set listener for list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Cursor selectedItem = (Cursor) listView.getItemAtPosition(position);
                String itemRowId = selectedItem.getString(selectedItem.getColumnIndexOrThrow(MarbleDBContract.Expenses._ID));
                Toast.makeText(getApplicationContext(), itemRowId, Toast.LENGTH_SHORT).show();
                // @todo: send entry data to new activity where it can be updated
            }
        });
    }

    private void readFromDB() {
        EditText searchBox = findViewById(R.id.editText_dateSearch);

        long date = 0;
        String response = "";
        SQLiteDatabase database = new MarbleDBHelper(this).getReadableDatabase();
        Cursor cursor = null;

        if (searchBox.getText().toString().equals("")) {
            cursor = database.rawQuery(
                    "select * from " + MarbleDBContract.Expenses.TABLE_NAME +
                            " order by " + MarbleDBContract.Expenses.COLUMN_DATE + " desc",
                    null);
            // feed database response into Cursor adapter
            String[] fromColums = {
                    MarbleDBContract.Expenses.COLUMN_AMOUNT,
                    MarbleDBContract.Expenses.COLUMN_DESCRIPTION,
                    MarbleDBContract.Expenses.COLUMN_DATE
            };
            int[] toViews = {
                    R.id.textViewAmt,
                    R.id.textViewDesc,
                    R.id.textViewDate
            };
            SimpleCursorAdapter adapter = new MarbleCursorAdapter(this,
                    R.layout.database_read_item, cursor, fromColums, toViews, 0);
            ListView listView = findViewById(R.id.listViewDB);
            listView.setAdapter(adapter);
        } else {
            // check to see if valid date was entered
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
                        searchBox.getText().toString()));
                date = calendar.getTimeInMillis();

                // exception wasn't thrown, start building db query
                String selection =  MarbleDBContract.Expenses.COLUMN_DATE + " == ?";

                String[] selectionArgs = {date + ""};

                cursor = database.query(
                        MarbleDBContract.Expenses.TABLE_NAME,     // The table to query
                        null,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                      // don't sort
                );
                // feed database response into Cursor adapter
                String[] fromColums = {
                        MarbleDBContract.Expenses.COLUMN_AMOUNT,
                        MarbleDBContract.Expenses.COLUMN_DESCRIPTION,
                        MarbleDBContract.Expenses.COLUMN_DATE
                };
                int[] toViews = {
                        R.id.textViewAmt,
                        R.id.textViewDesc,
                        R.id.textViewDate
                };
                SimpleCursorAdapter adapter = new MarbleCursorAdapter(this,
                        R.layout.database_read_item, cursor, fromColums, toViews, 0);
                ListView listView = findViewById(R.id.listViewDB);
                listView.setAdapter(adapter);
            }
            catch (Exception e) {
//                Toast.makeText(this, "invalid date input!", Toast.LENGTH_SHORT).show();
                // was not a date, search descriptions instead:
                String selection =  MarbleDBContract.Expenses.COLUMN_DESCRIPTION + " == ?";
                String[] selectionArgs = {searchBox.getText().toString() + ""};

                cursor = database.query(
                        MarbleDBContract.Expenses.TABLE_NAME,     // The table to query
                        null,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        MarbleDBContract.Expenses.COLUMN_DATE + " desc" // order from latest to oldest
                );
                // feed database response into Cursor adapter
                String[] fromColums = {
                        MarbleDBContract.Expenses.COLUMN_AMOUNT,
                        MarbleDBContract.Expenses.COLUMN_DESCRIPTION,
                        MarbleDBContract.Expenses.COLUMN_DATE
                };
                int[] toViews = {
                        R.id.textViewAmt,
                        R.id.textViewDesc,
                        R.id.textViewDate
                };
                SimpleCursorAdapter adapter = new MarbleCursorAdapter(this,
                        R.layout.database_read_item, cursor, fromColums, toViews, 0);
                ListView listView = findViewById(R.id.listViewDB);
                listView.setAdapter(adapter);
            }
        }
    }
}
