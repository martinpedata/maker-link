package com.example.makerlink.threads.list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R;
import com.example.makerlink.navigation_pages.chats.Chat;
import com.example.makerlink.threads.post.ThreadActivity;

import java.util.ArrayList;
import java.util.List;

public class ThreadRecyclerViewAdapter extends RecyclerView.Adapter<ThreadRecyclerViewAdapter.MyViewHolder> {

    public Context context;
    public ArrayList<ThreadRecyclerModel> threads;
    public ArrayList<ThreadRecyclerModel> threadsFull;
    public ThreadRecyclerViewAdapter(Context context, ArrayList<ThreadRecyclerModel> threads) {
        this.context = context;
        this.threads = threads != null ? threads : new ArrayList<>();
        assert threads != null;
        this.threadsFull = new ArrayList<>(threads);
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
            Intent intent = new Intent(context, ThreadActivity.class);
            intent.putExtra("threadName", threads.get(position).getNameLongThread());
            intent.putExtra("threadID", threads.get(position).getID());
            intent.putExtra("threadAuthor", threads.get(position).getAuthorID()); //These are not in the myViewHolder, because they are attributes of the Model class, without being defined in a view.
            intent.putExtra("threadDomain", threads.get(position).getDomainID());
            intent.putExtra("threadDate", threads.get(position).getDateThread());
            intent.putExtra("threadDocument", threads.get(position).getThreadDocument());
//            intent.putExtra("threadThumbnail", threads.get(position).getBase64Image());
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

    /// Filter items
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ThreadRecyclerModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) { /// THIS ENSURES THAT THE FILTERED LIST TAKES UP THE FULL VALUE IN CASE NO SEARCH IS DONE
                filteredList.addAll(threadsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ThreadRecyclerModel thread : threadsFull) {
                    if (thread.getNameLongThread().toLowerCase().contains(filterPattern)) {
                        filteredList.add(thread);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            threads.clear();
            threads.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
    public Filter getFilter() {
        return filter;
    }
    public void updateFullList() {
        threadsFull.clear();
        threadsFull.addAll(threads);
    }

}
