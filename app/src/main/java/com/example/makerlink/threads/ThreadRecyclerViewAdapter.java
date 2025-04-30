package com.example.makerlink.threads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R;

import java.util.ArrayList;

public class ThreadRecyclerViewAdapter extends RecyclerView.Adapter<ThreadRecyclerViewAdapter.MyViewHolder> {

    public Context context;
    public ArrayList<ThreadModel> threads;

    public ThreadRecyclerViewAdapter(Context context, ArrayList<ThreadModel> threads) {
        this.context = context;
        this.threads = threads;
    }

    /// This is what makes the recycler view visible
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_thread_item, parent, false);

        return new ThreadRecyclerViewAdapter.MyViewHolder(view);
    }

    /// This is where you need the ThreadModel class. To retrieve the info for the views bonded in myViewHolder.

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nameThread.setText(threads.get(position).getNameThread());
        holder.authorThread.setText(threads.get(position).getAuthorThread());
        holder.dateThread.setText(threads.get(position).getDateThread());
        holder.hashtagThread.setText(threads.get(position).getHashtagThread());
        holder.imageThread.setImageResource(threads.get(position).getImageThread());
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    /// This class is where you bind the views to respective variables
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageThread;
        public TextView nameThread, authorThread, hashtagThread, dateThread;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageThread = itemView.findViewById(R.id.imageItem);
            nameThread = itemView.findViewById(R.id.nameItem);
            authorThread = itemView.findViewById(R.id.authorItem);
            hashtagThread = itemView.findViewById(R.id.hashtagItem);
            dateThread = itemView.findViewById(R.id.dateItem);
        }
    }
}
