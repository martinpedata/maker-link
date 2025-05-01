package com.example.makerlink.ui.chats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView textSender;
        public TextView textMessage;
        public MessageViewHolder(View v) {
            super(v);
            textSender = v.findViewById(R.id.textViewSender);
            textMessage = v.findViewById(R.id.textViewMessage);
        }
    }

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item , parent, false);
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
