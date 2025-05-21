package com.example.makerlink.threads;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.example.makerlink.threads.list.ThreadRecyclerViewAdapter;
import com.example.makerlink.threads.post.CreateThreadActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentsViewAdapter commentsAdapter;
    private String content;
    private EditText contentComment;
    private ArrayList<CommentModel> commentItems = new ArrayList<>();
    private FloatingActionButton createButton;
    private RequestQueue requestQueue;
    private String username;
    private int userID;
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

        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        userID =  sharedPreferences.getInt("user_ID", -1);
        username = sharedPreferences.getString("UserName", "null");
        System.out.println(username);
        threadID = getIntent().getIntExtra("threadID", -1);
        recyclerView = findViewById(R.id.commentsRecycler);
        createButton = findViewById(R.id.createCommentButton);
        contentComment = findViewById(R.id.editTextComment);

        requestQueue = Volley.newRequestQueue(this);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = contentComment.getText().toString();
                createComment("https://studev.groept.be/api/a24pt215/InsertComment");
            }
        });
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

    public void createComment(String requestURL) {
        //Start an animating progress widget
        ProgressDialog progressDialog = new ProgressDialog(CommentsActivity.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();

        //Execute the Volley call. Note that we are not appending the image string to the URL, that happens further below
        StringRequest submitRequest = new StringRequest (Request.Method.POST, requestURL,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Turn the progress widget off
                commentItems.add(new CommentModel(0,username,content,0));
                contentComment.setText("");
                commentsAdapter.notifyDataSetChanged(); // Update the RecyclerView
                progressDialog.dismiss();
                Toast.makeText(CommentsActivity.this, "New Comment Posted !", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommentsActivity.this, "Failed to post new Comment ", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("thread", String.valueOf(threadID));
                params.put("content", content);
                params.put("likes", String.valueOf(0));
                params.put("author", String.valueOf(userID));
                return params;
            }
        };

        requestQueue.add(submitRequest);
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