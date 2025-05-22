package com.example.makerlink.navigation_pages.chats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R; //hello

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messages;
    private String currentUser;
    private static final int TYPE_DATE_HEADER = 0;
    private static final int TYPE_MESSAGE = 1;

    public MessageAdapter(List<Message> messages, String currentUser) {
        this.messages = messages;
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        if (msg.isDateHeader()) {
            return TYPE_DATE_HEADER;
        } else if (msg.getSender() != null && msg.getSender().equalsIgnoreCase(currentUser)) {
            return 2; // Self message
        } else {
            return 3; // Other message
        }
    }

    // ViewHolder for date headers
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView dateText;

        public HeaderViewHolder(View v) {
            super(v);
            dateText = v.findViewById(R.id.textViewDateHeader);
        }
    }

    // ViewHolder for messages
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView textSender;
        public TextView textMessage;

        public MessageViewHolder(View v) {
            super(v);
            textSender = v.findViewById(R.id.textViewSender);
            textMessage = v.findViewById(R.id.textViewMessage);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_DATE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.date_header_item, parent, false));
            case 2: // Self message
                return new MessageViewHolder(inflater.inflate(R.layout.message_item_other, parent, false));
            case 3: // Other message
            default:
                return new MessageViewHolder(inflater.inflate(R.layout.message_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).dateText.setText(msg.getMessage());
        } else if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).textSender.setText(msg.getSender());
            ((MessageViewHolder) holder).textMessage.setText(msg.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
