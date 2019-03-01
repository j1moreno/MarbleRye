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
    private RecycleRowData mRowData;

    public interface OnItemClickListener {
        void onItemClick(HistoryData data);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        // RecycleRowData object contains the data to be defined
        private RecycleRowData rowData;

        public MyViewHolder(View v, RecycleRowData rd) {
            super(v);
            rowData = rd;
            rowData.bindViews(v);
        }

        public void bind(final HistoryData item, final OnItemClickListener listener) {
            rowData.bindData(item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }


    public MarbleRecycleAdapter(ArrayList<HistoryData> myDataset, int layout, RecycleRowData rowData) {
        mDataset = myDataset;
        rowLayoutId = layout;
        mRowData = rowData;
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
        return new MyViewHolder(v, mRowData);
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

