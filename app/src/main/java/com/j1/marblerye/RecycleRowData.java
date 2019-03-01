package com.j1.marblerye;

import android.view.View;
import android.widget.TextView;

public abstract class RecycleRowData {
    protected TextView description;
    protected TextView date;
    protected TextView amount;

    public RecycleRowData() {

    }

    public abstract void bindViews(View view);
    public abstract void bindData(HistoryData historyData);
}
