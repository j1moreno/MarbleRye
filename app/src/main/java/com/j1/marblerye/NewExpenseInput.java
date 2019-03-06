package com.j1.marblerye;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
    private double incrementAmount = 1.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense_input);

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
        SQLiteDatabase database = new MarbleDBHelper(this).getWritableDatabase();

        // get data from inputs
        EditText amountInput = findViewById(R.id.editText_amount);
        EditText descriptionInput = findViewById(R.id.editText_description);
        TextView dateInput = findViewById(R.id.editExpense_editText_date);

        ContentValues values = new ContentValues();
        values.put(MarbleDBContract.Expenses.COLUMN_AMOUNT, amountInput.getText().toString());
        // check to see if description has been entered, otherwise don't add entry
        if (descriptionInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Must enter a description!", Toast.LENGTH_LONG).show();
            return;
        }
        values.put(MarbleDBContract.Expenses.COLUMN_DESCRIPTION, descriptionInput.getText().toString().trim());
        // make sure date is properly entered, otherwise don't add entry
        try {
            long date = MarbleUtils.convertDateToLong(this, dateInput.getText().toString());
            values.put(MarbleDBContract.Expenses.COLUMN_DATE, date);
        }
        catch (Exception e) {
            Toast.makeText(this, "Date is in the wrong format", Toast.LENGTH_LONG).show();
            return;
        }
        // got to this line, so it's OK to add entry to database
        long newRowId = database.insert(MarbleDBContract.Expenses.TABLE_NAME, null, values);

        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void increaseAmount() {
        EditText editTextAmount = findViewById(R.id.editText_amount);
        double currentValue = Double.valueOf(editTextAmount.getText().toString());
        double newValue = currentValue + incrementAmount;
        editTextAmount.setText(String.format("%.2f", newValue));
    }

    private void decreaseAmount() {
        EditText editTextAmount = findViewById(R.id.editText_amount);
        double currentValue = Double.valueOf(editTextAmount.getText().toString());
        if (currentValue <= 0) return;
        double newValue = currentValue - incrementAmount;
        editTextAmount.setText(String.format("%.2f", newValue));
    }
}
