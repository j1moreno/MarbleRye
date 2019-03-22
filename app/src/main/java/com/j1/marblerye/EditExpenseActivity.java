package com.j1.marblerye;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity {

    private EditText amount;
    private EditText description;
    private EditText date;
    private String id;
    private SQLiteDatabase database;

    private double changeAmount = 1.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit Expense");
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
        // set frequently used buttons
        database = new MarbleDBHelper(this).getWritableDatabase();
        String [] mostUsedDescriptions = MarbleCalculator.getMostUsedDescriptions(database, 3);
        // make sure we have at least 3 entries, otherwise write default values:
        if (mostUsedDescriptions.length < 3) {
            String [] defaultDesciptions = {"Lunch", "Gas", "Drinks"};
            mostUsedDescriptions = defaultDesciptions;
        }
        Button mostUsed1 = findViewById(R.id.editExpense_button_mostUsed1);
        Button mostUsed2 = findViewById(R.id.editExpense_button_mostUsed2);
        Button mostUsed3 = findViewById(R.id.editExpense_button_mostUsed3);
        mostUsed1.setText(mostUsedDescriptions[0]);
        mostUsed2.setText(mostUsedDescriptions[1]);
        mostUsed3.setText(mostUsedDescriptions[2]);
        // set listeners for buttons
        mostUsed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                description.setText(button.getText().toString());
            }
        });
        mostUsed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                description.setText(button.getText().toString());
            }
        });
        mostUsed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                description.setText(button.getText().toString());
            }
        });
        date = findViewById(R.id.editExpense_editText_date);
        // can't edit this text by typing, only using date picker
        date.setInputType(InputType.TYPE_NULL);
        date.setText(MarbleUtils.convertLongToDate(extras.getLong("DATE"), getString(R.string.date_format_pattern)));
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // bundle current date so that date picker defaults to current date
                Bundle bundle = new Bundle();
                bundle.putLong("CURRENT_DATE", MarbleUtils.convertDateToLong(getApplicationContext(), date.getText().toString()));
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setDateSetListener(new DatePickerFragment.MarbleDateSetListener() {
                    @Override
                    public void onSet(DatePicker view, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        String formattedDate = new SimpleDateFormat(getString(R.string.date_format_pattern), Locale.US).format(calendar.getTime());
                        date.setText(formattedDate);
                    }
                });
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "datePicker");

            }
        });
        Button buttonSaveToDB = findViewById(R.id.button);
        buttonSaveToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NewExpenseInput.isInputDataValid(getApplicationContext(), amount, description, date)) {
                    // if input data is not valid, stop here and return
                    return;
                }
                // save updated values to database
                ContentValues values = new ContentValues();
                values.put(MarbleDBContract.Expenses.COLUMN_AMOUNT, amount.getText().toString());
                values.put(MarbleDBContract.Expenses.COLUMN_DESCRIPTION, description.getText().toString().trim());
                long longDate = MarbleUtils.convertDateToLong(getApplicationContext(), date.getText().toString());
                values.put(MarbleDBContract.Expenses.COLUMN_DATE, longDate);
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
