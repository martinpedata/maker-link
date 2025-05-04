package com.example.makerlink.ui.chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R; //hello

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    private String currentUser;
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView textSender;
        public TextView textMessage;
        public MessageViewHolder(View v) {
            super(v);
            textSender = v.findViewById(R.id.textViewSender);
            textMessage = v.findViewById(R.id.textViewMessage);
        }
    }

    public MessageAdapter(List<Message> messages,String currentUserId) {
        this.messages = messages;
        this.currentUser = currentUserId;
    }
    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(currentUser)) {
            return 1; // right (me)
        } else {
            return 0; // left (other)
        }
    }
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item_other, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item, parent, false);
        }
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.textMessage.setText(messages.get(position).getMessage());
        holder.textSender.setText(messages.get(position).getSender());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
