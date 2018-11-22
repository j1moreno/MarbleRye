package com.j1.marblerye;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewExpenseInput extends AppCompatActivity {

    private static final String TAG = "NewExpenseActivity";
    private int incrementAmount = 1;

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
        EditText editTextDate = findViewById(R.id.editText_date);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        editTextDate.setText(dateFormat.format(date));
    }

    private void saveToDB() {
        SQLiteDatabase database = new BreadLoafDBHelper(this).getWritableDatabase();

        // get data from inputs
        EditText amountInput = findViewById(R.id.editText_amount);
        EditText descriptionInput = findViewById(R.id.editText_description);
        EditText dateInput = findViewById(R.id.editText_date);

        ContentValues values = new ContentValues();
        values.put(BreadLoafDBContract.Expenses.COLUMN_AMOUNT, amountInput.getText().toString());
        values.put(BreadLoafDBContract.Expenses.COLUMN_DESCRIPTION, descriptionInput.getText().toString());

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
                    dateInput.getText().toString()));
            long date = calendar.getTimeInMillis();
            values.put(BreadLoafDBContract.Expenses.COLUMN_DATE, date);
        }
        catch (Exception e) {
            Log.e(TAG, "Error", e);
            Toast.makeText(this, "Date is in the wrong format", Toast.LENGTH_LONG).show();
            return;
        }
        long newRowId = database.insert(BreadLoafDBContract.Expenses.TABLE_NAME, null, values);

        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
    }

    private void increaseAmount() {
        EditText editTextAmount = findViewById(R.id.editText_amount);
        double currentValue = Double.valueOf(editTextAmount.getText().toString());
        double newValue = currentValue + incrementAmount;
        editTextAmount.setText(String.valueOf(newValue));
    }

    private void decreaseAmount() {
        EditText editTextAmount = findViewById(R.id.editText_amount);
        double currentValue = Double.valueOf(editTextAmount.getText().toString());
        if (currentValue <= 0) return;
        double newValue = currentValue - incrementAmount;
        editTextAmount.setText(String.valueOf(newValue));
    }
}
