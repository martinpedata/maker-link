package com.example.makerlink.threads.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.example.makerlink.threads.post.CreateThreadActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserThreadRecyclerActivity extends AppCompatActivity {
    private FloatingActionButton createThreadButton;
    private RecyclerView recyclerView;
    private TextView heading;
    private int userID;
    private ArrayList<ThreadRecyclerModel> threadItems;
    private RequestQueue requestQueue;

    private ThreadRecyclerViewAdapter threadAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_thread_recycler);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        threadItems = new ArrayList<>();

        heading = findViewById(R.id.headingRecyclerUser);
        createThreadButton = findViewById(R.id.addThread);
        recyclerView = findViewById(R.id.my_recycler_userThreads);

        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        userID = sharedPreferences.getInt("user_ID", -1);

        createThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserThreadRecyclerActivity.this, CreateThreadActivity.class);
                startActivity(i);
            }
        });

        threadAdapter = new ThreadRecyclerViewAdapter(this, threadItems);
        recyclerView.setAdapter(threadAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpThread("https://studev.groept.be/api/a24pt215/RetrieveUserThreads/" + userID);
    }

    public void setUpThread(String requestURL) {
        System.out.println("inside setUpThread");
        threadItems.clear();  // <-- THIS IS ESSENTIAL!
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        heading.setText("Your Threads");
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject o = response.getJSONObject(i);
                                int id = o.getInt("id");
                                String nameThread = o.getString("name");

                                String nameThreadShort = "";
                                //Needed because we want the full name when we open the activity and short name when in scrolling mode.
                                if (nameThread.length() > 14) {
                                    nameThreadShort = nameThread.substring(0,12) + "...";
                                }
                                else {
                                    nameThreadShort = nameThread;
                                }
                                int authorID = o.getInt("author_id");
                                int domainID = o.getInt("domain_id");
                                String creationDate = o.getString("creationdate").substring(0,10);
                                String threadDocument = o.getString("document");
                                String b64Image = o.getString("image_resource");
                                threadItems.add(new ThreadRecyclerModel(id, nameThread, nameThreadShort, b64Image, creationDate, authorID, domainID, threadDocument));
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                                System.out.println("json array problem");
                            }
                        }
                        recyclerView.scrollToPosition(0);
                        threadAdapter.notifyDataSetChanged(); // Update the RecyclerView
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("inside onError");
                        Log.e("ErrorThreadCreazione", error.getLocalizedMessage());
                    }
                }
        );
        requestQueue.add(submitRequest);
    }
}