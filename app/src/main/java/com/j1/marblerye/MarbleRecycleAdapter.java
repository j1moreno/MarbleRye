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
    private int rowLayoutId;
    private OnItemClickListener mListener;
    private RecycleRowData mRowData;
    private TwoItemHolder mHolder;

    public interface OnItemClickListener {
        void onItemClick(HistoryData data);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static abstract class ItemHolder extends RecyclerView.ViewHolder {

        // RecycleRowData object contains the data to be defined
        public TextView description;
        public TextView date;
        public TextView amount;

        public ItemHolder(View v) {
            super(v);
        }

        public abstract void bind(HistoryData item, final OnItemClickListener listener);
    }

    public static class TwoItemHolder extends ItemHolder {

        // RecycleRowData object contains the data to be defined
        public RecycleRowData rowData;

        public TwoItemHolder(View v, RecycleRowData rd) {
            super(v);
            rowData = rd;
            rowData.bindViews(v);
        }

        public TwoItemHolder(View v) {
            super(v);
            date = v.findViewById(R.id.history_description);
            amount = v.findViewById(R.id.history_amount);
        }

        public void bind(final HistoryData item, final OnItemClickListener listener) {
            date.setText(item.date);
            amount.setText(item.amount);
            Log.d("ADAPTER", item.date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
//                    Log.d("ADAPTER", item.date);
                }
            });

        }
    }

    public static class ThreeItemHolder extends ItemHolder {

        public ThreeItemHolder(View v) {
            super(v);
            description = v.findViewById(R.id.textViewDesc);
            amount = v.findViewById(R.id.textViewAmt);
            date = v.findViewById(R.id.textViewDate);
        }

        public void bind(final HistoryData item, final OnItemClickListener listener) {
            description.setText(item.description);
            date.setText(item.date);
            amount.setText(item.amount);
            Log.d("ADAPTER", item.date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
//                    Log.d("ADAPTER", item.date);
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

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

