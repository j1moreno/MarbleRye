package com.j1.marblerye;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MarbleRecycleAdapter extends RecyclerView.Adapter<MarbleRecycleAdapter.MyViewHolder> {
    private ArrayList<HistoryData> mDataset;
    private int rowLayoutId;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView description, amount;
        public MyViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.history_description);
            amount = v.findViewById(R.id.history_amount);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MarbleRecycleAdapter(ArrayList<HistoryData> myDataset, int layout) {
        mDataset = myDataset;
        rowLayoutId = layout;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MarbleRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(rowLayoutId, parent, false);
        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.description.setText(mDataset.get(position).date);
        holder.amount.setText(mDataset.get(position).amount);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

