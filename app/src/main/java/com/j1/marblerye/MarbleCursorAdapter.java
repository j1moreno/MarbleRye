package com.j1.marblerye;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MarbleCursorAdapter extends SimpleCursorAdapter{

    public MarbleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        // convert date from MS to dd/mm/yyyy format:
        TextView date = view.findViewById(R.id.textViewDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(date.getText().toString()));
        date.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
    }
}
