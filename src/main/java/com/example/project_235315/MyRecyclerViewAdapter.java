package com.example.project_235315;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Task> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Task> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;


    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String task = mData.get(position).taskDesc;
        holder.myTextView.setText(task);
        if(mData.get(position).isDone){
            holder.myLayout.setBackgroundColor(Color.CYAN);
        }
        else
        {
            holder.myLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        View myLayout;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvTask);
            myLayout = itemView.findViewById(R.id.layout);
            itemView.setOnClickListener(this);

            ImageButton btnDelete = (ImageButton)itemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (mClickListener != null) mClickListener.onDeleteButtonClick(view, getAdapterPosition());
                }
            });

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Task getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onDeleteButtonClick(View view, int position);
    }
}