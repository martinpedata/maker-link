package com.example.makerlink.threads;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.example.makerlink.threads.list.ThreadRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentsViewAdapter commentsAdapter;
    private ArrayList<CommentModel> commentItems = new ArrayList<>();
    private RequestQueue requestQueue;
    private int threadID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        threadID = getIntent().getIntExtra("threadID", -1);
        recyclerView = findViewById(R.id.commentsRecycler);

        requestQueue = Volley.newRequestQueue(this);

        /// Execute some function to populate comments
        commentsAdapter = new CommentsViewAdapter(this, commentItems);
        recyclerView.setAdapter(commentsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveComments("https://studev.groept.be/api/a24pt215/RetrieveCommentsOfThread/" + threadID);
    }

    public void retrieveComments(String requestURL) {
        commentItems.clear();
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i<response.length(); i++) {
                            try {
                                JSONObject o = response.getJSONObject(i);
                                String content = o.getString("content");
                                int likes = o.getInt("likes");
                                String username = o.getString("username");
                                int id = o.getInt("id");
                                commentItems.add(new CommentModel(id,username,content,likes));

                                commentsAdapter.notifyDataSetChanged(); // Update the RecyclerView
                            }
                            catch (JSONException e) {

                            }
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
}