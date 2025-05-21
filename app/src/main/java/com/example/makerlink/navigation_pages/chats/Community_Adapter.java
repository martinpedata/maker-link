package com.example.makerlink.navigation_pages.chats;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Community_Adapter extends RecyclerView.Adapter<Community_Adapter.CommunityViewHolder> implements Filterable {
    private List<Chat> chatList;
    private OnClickChat listener;
    private List<Chat> chatListFull;
    public Context context;

    public Community_Adapter(List<Chat> chatList, OnClickChat listener) {
        this.chatList = chatList != null ? chatList : new ArrayList<>();
        this.listener = listener;
        this.chatListFull = new ArrayList<>(chatList); // To support filtering
    }

    // ViewHolder class to hold reference to views for each item
    public static class CommunityViewHolder extends RecyclerView.ViewHolder {
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
        holder.imageView.setImageBitmap(chat.getImagebitmap());  // Or set an actual image
        holder.itemView.setOnClickListener(v -> {
            ispresent(chat.getId());
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatName", chat.getName());
            intent.putExtra("chat_id", chat.getId());
            Bitmap bitmap = chat.getImagebitmap();
            String imagePath = saveImageToInternalStorage(v.getContext(), bitmap);
            intent.putExtra("imagePath", imagePath);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // Method to add a new community to the list and notify the adapter
    public void addCommunity(Chat newChat) {
        chatList.add(newChat);
        chatListFull.add(newChat);  // Add to the full list for filtering
        notifyItemInserted(chatList.size() - 1);  // Notify that an item was inserted
    }

    // Method to clear the list and add new data (if needed)
    public void setCommunityList(List<Chat> newChatList) {
        chatList.clear();
        chatList.addAll(newChatList);
        chatListFull.clear();
        chatListFull.addAll(newChatList); // Update full list for filtering
        notifyDataSetChanged(); // Notify that the data has changed
    }

    // Filter implementation for search functionality
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

    public void ispresent(int chatId) {
        // Assuming you have user_id stored in shared preferences or passed to the adapter
        SharedPreferences sharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_ID", -1); // Fetch user_id from shared preferences

        // Check if user_id is valid
        if (userId == -1) {
            Log.e("Community_Adapter", "User ID is missing.");
            return;
        }

        // Construct the URL for your backend API
        String url = "https://studev.groept.be/api/a24pt215/PresentInChat";  // Replace with actual URL

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("MessagePost", "Response: " + response);
                    // Handle successful presence update
                    Toast.makeText(context, "You are now marked as present in the chat!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("VolleyError", "Error: " + errorMsg);
                        Toast.makeText(context, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("VolleyError", "Unknown error occurred");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userval", String.valueOf(userId));  // Send user_id
                params.put("chatval", String.valueOf(chatId));  // Send chat_id
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }
    private String saveImageToInternalStorage(Context context, Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File imagePath = new File(directory, "profileImage_" + System.currentTimeMillis() + ".jpg"); // unique filename

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath.getAbsolutePath();
    }
}
