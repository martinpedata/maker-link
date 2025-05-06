package com.example.makerlink.threads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.ImageHandling;
import com.example.makerlink.R;

import java.util.ArrayList;

public class ThreadRecyclerViewAdapter extends RecyclerView.Adapter<ThreadRecyclerViewAdapter.MyViewHolder> {

    public Context context;
    public ArrayList<ThreadRecyclerModel> threads;

    public ThreadRecyclerViewAdapter(Context context, ArrayList<ThreadRecyclerModel> threads) {
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
        holder.nameThread.setText(threads.get(position).getNameShortThread());
        holder.dateThread.setText(threads.get(position).getDateThread());
        holder.imageThread.setImageBitmap(threads.get(position).getBitmapImage()); //base64ToBitMap() is a helper method found below that returns a bit map (an image).

        /// Open ThreadActivity from recycler view. Variable position represents the thread item which is currently being handled.
        /// USE OF LAMBDA EXPRESSION !!!
        holder.threadItem.setOnClickListener(e->{
            System.out.println("click registered");
            Intent intent = new Intent(context,ThreadActivity.class);
            intent.putExtra("threadName", threads.get(position).getNameLongThread());
            intent.putExtra("threadAuthor", threads.get(position).getAuthorID()); //These are not in the myViewHolder, because they are attributes of the Model class, without being defined in a view.
            intent.putExtra("threadDomain", threads.get(position).getDomainID());
            intent.putExtra("threadDate", threads.get(position).getDateThread());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    /// This class is where you bind the views to respective variables
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView threadItem;
        public ImageView imageThread;
        public TextView nameThread, dateThread;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            threadItem = itemView.findViewById(R.id.threadItem);
            imageThread = itemView.findViewById(R.id.imageItem);
            nameThread = itemView.findViewById(R.id.nameItem);
            dateThread = itemView.findViewById(R.id.dateItem);
        }
    }
}
