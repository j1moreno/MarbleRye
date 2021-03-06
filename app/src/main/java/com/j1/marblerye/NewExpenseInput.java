package com.j1.marblerye;

import android.content.ContentValues;
import android.content.Context;
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
import java.util.Date;
import java.util.Locale;

public class NewExpenseInput extends AppCompatActivity {

    private static final String TAG = "NewExpenseActivity";
    private final double incrementAmount = 1.00;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense_input);
        setTitle("New Expense");

        Button addToDB = findViewById(R.id.button);
        addToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDB();
            }
        });

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

        final EditText description = findViewById(R.id.editText_description);

        // set frequently used buttons
        database = new MarbleDBHelper(this).getWritableDatabase();
        String [] mostUsedDescriptions = MarbleCalculator.getMostUsedDescriptions(database, 3);
        // make sure we have at least 3 entries, otherwise write default values:
        if (mostUsedDescriptions[mostUsedDescriptions.length-1] == null) {
            // if last element is null, not enough descriptions to show most used yet, so show default
            mostUsedDescriptions = new String[] {"Lunch", "Gas", "Drinks"};
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

        // set default date
        final EditText textViewDate = findViewById(R.id.editExpense_editText_date);
        textViewDate.setInputType(InputType.TYPE_NULL);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format_pattern), Locale.US);
        textViewDate.setText(dateFormat.format(date));
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("CURRENT_DATE", MarbleUtils.convertDateToLong(getApplicationContext(), textViewDate.getText().toString()));
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setDateSetListener(new DatePickerFragment.MarbleDateSetListener() {
                    @Override
                    public void onSet(DatePicker view, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        String formattedDate = new SimpleDateFormat(getString(R.string.date_format_pattern), Locale.US).format(calendar.getTime());
                        textViewDate.setText(formattedDate);
                    }
                });
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "datePicker");

            }
        });
    }

    private void saveToDB() {
        // get data from inputs
        EditText amountInput = findViewById(R.id.editText_amount);
        EditText descriptionInput = findViewById(R.id.editText_description);
        TextView dateInput = findViewById(R.id.editExpense_editText_date);
        if (!isInputDataValid(this, amountInput, descriptionInput, dateInput)) {
            // if input data is not valid, stop here and return
            return;
        }
        // got to this line, so it's OK to add entry to database
        ContentValues values = new ContentValues();
        values.put(MarbleDBContract.Expenses.COLUMN_AMOUNT, amountInput.getText().toString());
        values.put(MarbleDBContract.Expenses.COLUMN_DESCRIPTION, descriptionInput.getText().toString().trim());
        values.put(MarbleDBContract.Expenses.COLUMN_DATE, MarbleUtils.convertDateToLong(this, dateInput.getText().toString()));
        long newRowId = database.insert(MarbleDBContract.Expenses.TABLE_NAME, null, values);

        Toast.makeText(this, "Expense #" + newRowId + " saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void increaseAmount() {
        EditText editTextAmount = findViewById(R.id.editText_amount);
        double currentValue = Double.valueOf(editTextAmount.getText().toString());
        double newValue = currentValue + incrementAmount;
        editTextAmount.setText(String.format(Locale.US, "%.2f", newValue));
    }

    private void decreaseAmount() {
        EditText editTextAmount = findViewById(R.id.editText_amount);
        double currentValue = Double.valueOf(editTextAmount.getText().toString());
        if (currentValue <= 0) return;
        double newValue = currentValue - incrementAmount;
        editTextAmount.setText(String.format(Locale.US, "%.2f", newValue));
    }

    public static boolean isInputDataValid(Context context, EditText amountInput, EditText descriptionInput, TextView dateInput) {
        // make sure amount entered is valid
        try {
            //noinspection unused: trying to parse, in order to force exception in case it's invalid
            double amount = Double.parseDouble(amountInput.getText().toString());
        } catch (Exception e) {
            Log.e(TAG, "unable to convert amount input to string.");
            return false;
        }
        // check to see if description has been entered, otherwise don't add entry
        if (descriptionInput.getText().toString().isEmpty()) {
            Toast.makeText(context, "Must enter a description!", Toast.LENGTH_LONG).show();
            return false;
        }
        // make sure date is properly entered, otherwise don't add entry
        long date = MarbleUtils.convertDateToLong(context, dateInput.getText().toString());

        // return true if date is not 0
        return date != 0;
    }
}
