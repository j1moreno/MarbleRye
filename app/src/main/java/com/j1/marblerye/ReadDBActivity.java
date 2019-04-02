package com.j1.marblerye;

import android.content.Intent;
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

public class ReadDBActivity extends AppCompatActivity {

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
                // get cursor for selected item
                Cursor selectedItem = (Cursor) listView.getItemAtPosition(position);
                // get all fields from cursor for editing
                String itemRowId = selectedItem.getString(selectedItem.getColumnIndexOrThrow(MarbleDBContract.Expenses._ID));
                String itemAmount = selectedItem.getString(selectedItem.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_AMOUNT));
                String itemDescription = selectedItem.getString(selectedItem.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DESCRIPTION));
                long itemDate = selectedItem.getLong(selectedItem.getColumnIndexOrThrow(MarbleDBContract.Expenses.COLUMN_DATE));
                // put fields into intent to pass to edit activity
                Intent intent = new Intent(ReadDBActivity.this, EditExpenseActivity.class);
                intent.putExtra("ID", itemRowId);
                intent.putExtra("AMOUNT", itemAmount);
                intent.putExtra("DESCRIPTION", itemDescription);
                intent.putExtra("DATE", itemDate);
                startActivity(intent);
            }
        });
    }

    protected void onResume() {
        // read database again in case anything changed
        readFromDB();
        super.onResume();
    }

    private void readFromDB() {
        EditText searchBox = findViewById(R.id.editText_dateSearch);

        SQLiteDatabase database = new MarbleDBHelper(this).getReadableDatabase();
        Cursor cursor;

        if (searchBox.getText().toString().equals("")) {
            cursor = database.rawQuery(
                    "select * from " + MarbleDBContract.Expenses.TABLE_NAME +
                            " order by " + MarbleDBContract.Expenses.COLUMN_DATE + " desc",
                    null);
        } else {
            cursor = database.query(
                    MarbleDBContract.Expenses.TABLE_NAME,
                    null,
                    MarbleDBContract.Expenses.COLUMN_DESCRIPTION + " LIKE ?",
                    new String[] {"%"+ searchBox.getText().toString().trim() + "%" },
                    null,
                    null,
                    MarbleDBContract.Expenses.COLUMN_DATE + " desc",
                    null);
        }
        // feed database response into Cursor adapter
        String[] fromColumns = {
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
                R.layout.database_read_item, cursor, fromColumns, toViews, 0);
        ListView listView = findViewById(R.id.listViewDB);
        listView.setAdapter(adapter);
    }
}
