package com.example.makerlink.navigation_pages.chats;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
    private List<Integer> joinedCommunityIds;
    private RequestQueue requestQueue;
    private int UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView1);
        searchView = findViewById(R.id.searchBar);

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        UserID = sharedPref.getInt("user_ID", -1);

        // Initialize list for user joined community IDs
        joinedCommunityIds = new ArrayList<>();

        // Fetch user's joined communities first
        getUserJoinedCommunities("https://studev.groept.be/api/a24pt215/RetrieveUserJoinedCommunities/");

        // Fetch available communities
        setUpCommunity("https://studev.groept.be/api/a24pt215/RetrieveCommunity");
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK);  // Set the text color to black
            searchEditText.setHintTextColor(Color.BLACK);  // Set the hint color to black
        }

        // Change color of the magnifying glass icon to black
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) {
            searchIcon.setImageResource(R.drawable.icon_search);
            searchIcon.setImageTintList(null);
            searchIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);  // Change icon color to black
        }

        // Change color of the close button to black
        ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeIcon != null) {
            closeIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);  // Change icon color to black
        }
        searchView.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) JoinActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        });
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (chatadaptor != null) {
                    chatadaptor.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (chatadaptor != null) {
                    chatadaptor.getFilter().filter(newText);
                }
                return false;
            }
        });
    }

    // Method to get the list of communities the user has already joined
    public void getUserJoinedCommunities(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);

        // Replace the URL with your API endpoint for retrieving joined communities
        String url = requestURL + UserID;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Clear the list before adding new data
                        joinedCommunityIds.clear();

                        // Parse response and collect joined community IDs
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject o = response.getJSONObject(i);
                                int communityId = o.getInt("community");  // Adjust to your API's response structure
                                joinedCommunityIds.add(communityId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Now filter available communities based on joined ones
                        filterAvailableCommunities();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Error fetching user communities: " + error.getMessage());
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    // Method to filter communities based on the user's joined communities
    public void filterAvailableCommunities() {
        List<Chat> filteredChatList = new ArrayList<>();

        // Filter out communities the user has already joined
        for (Chat chat : chatList) {
            if (!joinedCommunityIds.contains(chat.getId())) {
                filteredChatList.add(chat);
            }
        }

        // Update the adapter with filtered list of communities
        chatadaptor = new Joining_Adapter(filteredChatList, chat -> {
            int userId = UserID;
            int communityId = chat.getId();
            saveUserCommunityAssociation(userId, communityId);
            finish();
        });

        recyclerView.setAdapter(chatadaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(JoinActivity.this));
    }

    // Method to fetch the list of all available communities to join
    public void setUpCommunity(String requestURL) {
        chatList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse the response and add communities to the list
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject o = response.getJSONObject(i);
                                String namechat = o.getString("name");
                                int chat_id = o.getInt("id");
                                chatList.add(new Chat(namechat, chat_id));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Now filter the communities based on the ones the user has joined
                        filterAvailableCommunities();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Error fetching communities: " + error.getMessage());
                    }
                }
        );

        requestQueue.add(submitRequest);
    }

    // Method to save the user's association with a community
    private void saveUserCommunityAssociation(int userId, int communityId) {
        String url = "https://studev.groept.be/api/a24pt215/InsertCommunity";  // Replace with your actual API endpoint

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("InsertCommunity", "Response: " + response);
                    // Handle success
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
                params.put("user", String.valueOf(userId));  // Ensure backend matches this field name
                params.put("community", String.valueOf(communityId));  // Ensure backend matches this field name
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }
}