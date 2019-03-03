package com.j1.marblerye;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditExpenseActivity extends AppCompatActivity {

    private EditText amount;
    private EditText description;
    private EditText date;
    private String id;

    private double changeAmount = 1.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get extras from intent
        Bundle extras = getIntent().getExtras();
        // get row id for selected entry, we'll need this later
        id = extras.getString("ID", "");
        // layout is exactly new expense input, with a few tweaks
        setContentView(R.layout.activity_new_expense_input);
        // set EditText fields to be current entry values, passed from intent
        amount = findViewById(R.id.editText_amount);
        amount.setText(extras.getString("AMOUNT"));
        description = findViewById(R.id.editText_description);
        description.setText(extras.getString("DESCRIPTION"));
        date = findViewById(R.id.editText_date);
        date.setText(MarbleUtils.convertLongToDate(extras.getLong("DATE"), getString(R.string.date_format_pattern)));
        // Edit button to say update instead of add
        Button button = findViewById(R.id.button);
        button.setText("Update Entry");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MarbleDBContract.Expenses.COLUMN_AMOUNT, amount.getText().toString());
                // check to see if description has been entered, otherwise don't add entry
                if (description.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Must enter a description!", Toast.LENGTH_LONG).show();
                    return;
                }
                values.put(MarbleDBContract.Expenses.COLUMN_DESCRIPTION, description.getText().toString().trim());
                // make sure date is properly entered, otherwise don't add entry
                try {
                    long longDate = MarbleUtils.convertDateToLong(getApplicationContext(), date.getText().toString());
                    values.put(MarbleDBContract.Expenses.COLUMN_DATE, longDate);
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Date is in the wrong format", Toast.LENGTH_LONG).show();
                    return;
                }
                // get writable database to update entry:
                SQLiteDatabase database = new MarbleDBHelper(getApplicationContext()).getWritableDatabase();
                database.update(MarbleDBContract.Expenses.TABLE_NAME, values, "_id=" + id, null);
                Toast.makeText(getApplicationContext(), "Row " + id + " has been updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        // set click listeners for increment/decrement buttons:
        Button buttonPlus = findViewById(R.id.buttonPlus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseAmount();
            }
        });

        Button buttonMinus = findViewById(R.id.buttonMinus);
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseAmount();
            }
        });
    }

    private void increaseAmount() {
        EditText editTextAmount = findViewById(R.id.editText_amount);
        double currentValue = Double.valueOf(editTextAmount.getText().toString());
        double newValue = currentValue + changeAmount;
        editTextAmount.setText(String.format("%.2f", newValue));
    }

    private void decreaseAmount() {
        EditText editTextAmount = findViewById(R.id.editText_amount);
        double currentValue = Double.valueOf(editTextAmount.getText().toString());
        if (currentValue <= 0) return;
        double newValue = currentValue - changeAmount;
        editTextAmount.setText(String.format("%.2f", newValue));
    }
}
