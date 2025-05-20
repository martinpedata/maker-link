package com.example.makerlink.threads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.example.makerlink.playlists.PlaylistRecyclerAdapter;
import com.example.makerlink.playlists.PlaylistRecyclerModel;
import com.example.makerlink.threads.list.ThreadRecyclerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentsViewAdapter extends RecyclerView.Adapter<CommentsViewAdapter.MyViewHolder> {

    public Context context;
    ArrayList<CommentModel> comments;
    private RequestQueue requestQueue;

    public CommentsViewAdapter(Context context, ArrayList<CommentModel> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.comment_item, parent, false);

        return new CommentsViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CommentModel comment = comments.get(position);
        holder.likes.setText(String.valueOf(comment.getLikes())); ///CONVERT INTEGER INTO STRING !!!
        holder.contentText.setText(comment.getCommentContent());
        holder.authorText.setText(comment.getNameuser());
        if (comment.isUpvoted()) {
            holder.upvoteButton.setOnClickListener(e->{
                comment.removeLike();
                comment.setDownVoted();
                updateLikes("https://studev.groept.be/api/a24pt215/DecreaseLikesOfComment/" + comment.getID());
                holder.upvoteButton.setColorFilter(ContextCompat.getColor(context, R.color.teal_500), PorterDuff.Mode.SRC_IN);
            });
        }
        else {
            holder.upvoteButton.setOnClickListener(e->{
                comment.addLike();
                comment.setUpVoted();
                updateLikes("https://studev.groept.be/api/a24pt215/UpdateLikesOfComment/" + comment.getID());
                holder.upvoteButton.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            });
        }
    }

    private void updateLikes(String requestURL) {
        requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            System.out.println("success");
                            notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("ErrorThreadCreazione", error.getLocalizedMessage());
                        }
                    }
            );
            requestQueue.add(submitRequest);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageButton upvoteButton;
        public TextView likes, contentText, authorText;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            upvoteButton = itemView.findViewById(R.id.upvoteButton);
            likes = itemView.findViewById(R.id.commentLikes);
            contentText = itemView.findViewById(R.id.commentContentText);
            authorText = itemView.findViewById(R.id.commentAuthorText);
        }
    }
}