package com.example.makerlink.navigation_pages.chats;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R;


import java.util.ArrayList;
import java.util.List;


public class Community_Adapter extends RecyclerView.Adapter<Community_Adapter.CommunityViewHolder> implements Filterable {
    private List<Chat> chatList;
    private OnClickChat listener;
    private List<Chat> chatListFull;
    public Context context;

    public Community_Adapter(List<Chat> chatList, OnClickChat listener) {
        this.chatList = chatList != null ? chatList : new ArrayList<>();
        this.listener = listener;
        this.chatListFull = new ArrayList<>(chatList);
    }

    public static class CommunityViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textName;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textName = itemView.findViewById(R.id.textView);
        }
    }
    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.communities, parent, false);
        return new CommunityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.textName.setText(chat.getName());
        holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatName", chat.getName());
            intent.putExtra("chat_id", chat.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
    @Override
    public Filter getFilter() {
        return chatFilter;
    }

    private final Filter chatFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Chat> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(chatListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Chat chat : chatListFull) {
                    if (chat.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(chat);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            chatList.clear();
            chatList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
