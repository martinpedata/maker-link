package com.example.makerlink.navigation_pages.chats;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private Joining_Adapter chatadaptor;
    private List<Chat> chatList;
    private RequestQueue requestQueue;
    private int UserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerView1);
        searchView = findViewById(R.id.searchBar);
        SharedPreferences sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        UserID = sharedPref.getInt("user_ID", -1);
        setUpCommunity("https://studev.groept.be/api/a24pt215/RetrieveCommunity");
    }
    public void setUpCommunity(String requestURL) {
        chatList = new ArrayList<Chat>();
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                System.out.println("inside json array");
                                JSONObject o = response.getJSONObject(i);

                                String namechat = o.getString("name");
                                int chat_id = o.getInt("id");
                                chatList.add(new Chat(namechat, chat_id));
                            }
                            catch (JSONException e) {
                                System.out.println("error iterating json array");
                            }

                        }
                        chatadaptor = new Joining_Adapter(chatList, chat -> {
                            int userId = UserID;
                            int communityId = chat.getId();
                            saveUserCommunityAssociation(userId, communityId);
                            finish();
                        });
                        recyclerView.setAdapter(chatadaptor);
                        recyclerView.setLayoutManager(new LinearLayoutManager(JoinActivity.this));
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
    private void saveUserCommunityAssociation(int userId, int communityId) {
        String url = "https://studev.groept.be/api/a24pt215/InsertCommunity";  // Replace with your actual API endpoint

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("InsertCommunity", "Response: " + response);
                    // Handle success (e.g., show a success message or update UI)
                    Log.d("InsertCommunity", "Successfully joined community");
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("InsertCommunity", "Error: " + errorMsg);
                        Toast.makeText(JoinActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("InsertCommunity", "Unknown error occurred");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", String.valueOf(userId));  // must match backend expected name
                params.put("community", String.valueOf(communityId));  // must match backend expected name
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }
}