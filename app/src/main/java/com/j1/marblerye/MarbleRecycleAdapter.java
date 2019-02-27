package com.j1.marblerye;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MarbleRecycleAdapter extends RecyclerView.Adapter<MarbleRecycleAdapter.MyViewHolder> {
    private ArrayList<HistoryData> mDataset;
    private int rowLayoutId;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(HistoryData data);
    }

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

        public void bind(final HistoryData item, final OnItemClickListener listener) {
            description.setText(item.date);
            amount.setText(item.amount);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MarbleRecycleAdapter(ArrayList<HistoryData> myDataset, int layout) {
        mDataset = myDataset;
        rowLayoutId = layout;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(mDataset.get(position), mListener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

