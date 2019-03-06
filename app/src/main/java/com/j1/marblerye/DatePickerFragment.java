package com.j1.marblerye;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private MarbleDateSetListener dateSetListener;

    public interface MarbleDateSetListener {
        void onSet(DatePicker view, int year, int month, int day);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        Bundle bundle = getArguments();
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(bundle.getLong("CURRENT_DATE"));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        dateSetListener.onSet(view, year, month, day);
    }

    public void setDateSetListener(MarbleDateSetListener listener) {
        dateSetListener = listener;
    }
}
