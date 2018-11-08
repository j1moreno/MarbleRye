package com.j1.breadloaf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        Button getData = findViewById(R.id.button2);
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFromDB();
            }
        });
    }

    private void readFromDB() {
        EditText dateSearch = findViewById(R.id.editText_dateSearch);

        long date = 0;
        String response = "";

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
                    dateSearch.getText().toString()));
            date = calendar.getTimeInMillis();
        }
        catch (Exception e) {
            Toast.makeText(this, "invalid date input!", Toast.LENGTH_SHORT).show();
        }

        SQLiteDatabase database = new BreadLoafDBHelper(this).getReadableDatabase();

        String[] projection = {};

        String selection =  BreadLoafDBContract.Expenses.COLUMN_DATE + " == ?";

        String[] selectionArgs = {date + ""};

        Cursor cursor = database.query(
                BreadLoafDBContract.Expenses.TABLE_NAME,     // The table to query
                null,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );

//        Cursor cursor = database.rawQuery("select * from "+BreadLoafDBContract.Expenses.TABLE_NAME,null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0) + " | " + cursor.getString(1) + " | " + cursor.getString(2);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(cursor.getLong(
                        cursor.getColumnIndexOrThrow(BreadLoafDBContract.Expenses.COLUMN_DATE)));
                name += " | " + new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()) + "\n";
                response += name;
                cursor.moveToNext();
            }
            index = 0;
        }
//        Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        TextView textView = findViewById(R.id.textView);
        textView.setText(response);

    }
}
