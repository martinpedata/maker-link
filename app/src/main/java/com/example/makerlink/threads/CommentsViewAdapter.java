package com.example.makerlink.threads;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private int userID;

    public CommentsViewAdapter(Context context, ArrayList<CommentModel> comments) {
        this.context = context;
        this.comments = comments;
        SharedPreferences sharedPreferences = context.getSharedPreferences("myPref", MODE_PRIVATE);
        this.userID = sharedPreferences.getInt("user_ID", -1);
        requestQueue = Volley.newRequestQueue(context);
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

        holder.upvoteButton.setTag(comment.getID()); ///THE TAG IS NEEDED WHEN MAKING ASYNCHRONOUS CALLS IN ON BIND VIEW HOLDER BECAUSE THE HOLDER MIGHT BIND TO A NEW COMMENT BEFORE ASYNCHRONOUS CALL IS FINISHED.

        isUpVoted("https://studev.groept.be/api/a24pt215/IsUpvoted/"+ userID + "/" + comment.getID(), comment, holder, position);
    }

    public void isUpVoted(String requestURL, CommentModel comment, MyViewHolder holder, int position) {
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("success");
                        if (response.length() == 1) {
                            if (!holder.upvoteButton.getTag().equals(comment.getID())) return; // ensure holder still matches
                            holder.upvoteButton.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                            holder.upvoteButton.setOnClickListener(e -> {
                                updateLikesComment("https://studev.groept.be/api/a24pt215/DecreaseLikesOfComment/" + comment.getID());
                                updateLikesMapping("https://studev.groept.be/api/a24pt215/DeleteCommentUpvote/" + userID + "/" + comment.getID());
                                holder.upvoteButton.setColorFilter(ContextCompat.getColor(context, R.color.teal_500), PorterDuff.Mode.SRC_IN);
                                comment.removeLike();
                                notifyItemChanged(position);
                            });
                        }
                        else {
                            if (!holder.upvoteButton.getTag().equals(comment.getID())) return; // ensure holder still matches
                            holder.upvoteButton.setColorFilter(ContextCompat.getColor(context, R.color.teal_500), PorterDuff.Mode.SRC_IN);
                            holder.upvoteButton.setOnClickListener(e -> {
                                updateLikesComment("https://studev.groept.be/api/a24pt215/UpdateLikesOfComment/" + comment.getID());
                                updateLikesMapping("https://studev.groept.be/api/a24pt215/InsertIntoCommentUpvotes/" + userID + "/" + comment.getID());
                                holder.upvoteButton.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                comment.addLike();
                                notifyItemChanged(position);
                            });
                        }
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

    private void updateLikesComment(String requestURL) {
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            System.out.println("success");
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

    private void updateLikesMapping(String requestURL) {
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("success");
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