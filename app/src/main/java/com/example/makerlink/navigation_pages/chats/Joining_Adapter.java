package com.example.makerlink.navigation_pages.chats;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Joining_Adapter extends RecyclerView.Adapter<Joining_Adapter.CommunityViewHolder> implements Filterable {
    private List<Chat> chatList;
    private OnClickChat listener;
    private List<Chat> chatListFull;
    private Context context;
    private RequestQueue requestQueue;

    public Joining_Adapter(List<Chat> chatList, OnClickChat listener) {
        this.chatList = chatList != null ? chatList : new ArrayList<>();
        this.listener = listener;
        this.chatListFull = new ArrayList<>(chatList);
    }

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
        requestQueue = Volley.newRequestQueue(context);
        return new CommunityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.textName.setText(chat.getName());
        holder.imageView.setImageBitmap(chat.getImagebitmap());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(chat);
            }
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

    // Method to handle joining a community and saving the user-community relationship
    private void joinCommunity(int userId, int communityId) {
        String url = "https://your-api-url.com/saveUserCommunity";  // Replace with your actual API endpoint

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId);
            jsonBody.put("communityId", communityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle success (e.g., show a success message or update UI)
                        Log.d("JoinCommunity", "Successfully joined community");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("JoinCommunity", "Error joining community: " + error.getLocalizedMessage());
                    }
                });

        requestQueue.add(request);
    }
}