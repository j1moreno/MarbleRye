package com.j1.marblerye;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MarbleRecycleAdapter extends RecyclerView.Adapter<MarbleRecycleAdapter.ItemHolder> {
    private ArrayList<HistoryData> mDataset;
    private final int rowLayoutId;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(HistoryData data);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static abstract class ItemHolder extends RecyclerView.ViewHolder {

        TextView description;
        TextView date;
        TextView amount;

        ItemHolder(View v) {
            super(v);
        }

        protected abstract void bind(HistoryData item, final OnItemClickListener listener);
    }

    protected static class TwoItemHolder extends ItemHolder {

        TwoItemHolder(View v) {
            super(v);
            date = v.findViewById(R.id.history_description);
            amount = v.findViewById(R.id.history_amount);
        }

        public void bind(final HistoryData item, final OnItemClickListener listener) {
            date.setText(item.date);
            amount.setText(item.amount);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });

        }
    }

    protected static class ThreeItemHolder extends ItemHolder {

        ThreeItemHolder(View v) {
            super(v);
            description = v.findViewById(R.id.textViewDesc);
            amount = v.findViewById(R.id.textViewAmt);
            date = v.findViewById(R.id.textViewDate);
        }

        public void bind(final HistoryData item, final OnItemClickListener listener) {
            description.setText(item.description);
            date.setText(item.date);
            amount.setText(item.amount);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });

        }
    }


    public MarbleRecycleAdapter(ArrayList<HistoryData> myDataset, int layout) {
        mDataset = myDataset;
        rowLayoutId = layout;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent,
                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(rowLayoutId, parent, false);
        return (rowLayoutId == R.layout.database_read_item) ? new ThreeItemHolder(v) : new TwoItemHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(mDataset.get(position), mListener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setData(ArrayList<HistoryData> newDataset) {
        mDataset = newDataset;
    }
}

