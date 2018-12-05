package com.j1.marblerye;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditExpenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get extras from intent
        Bundle extras = getIntent().getExtras();
        // layout is exactly new expense input, with a few tweaks
        setContentView(R.layout.activity_new_expense_input);
        // set EditText fields to be current entry values, passed from intent
        EditText amount = findViewById(R.id.editText_amount);
        amount.setText(extras.getString("AMOUNT"));
        EditText description = findViewById(R.id.editText_description);
        description.setText(extras.getString("DESCRIPTION"));
        EditText date = findViewById(R.id.editText_date);
        date.setText(MarbleUtils.convertLongToDate(extras.getLong("DATE")));
        // Edit button to say update instead of add
        Button button = findViewById(R.id.button);
        button.setText("Update Entry");
        // @todo: add listener to update entry
    }
}
