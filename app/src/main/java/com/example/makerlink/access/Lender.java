package com.example.makerlink.access;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.MainActivity;
import com.example.makerlink.R;
import com.example.makerlink.navigation_pages.chats.Chat;
import com.example.makerlink.navigation_pages.chats.ChatActivity;
import com.example.makerlink.navigation_pages.chats.Community_Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lender extends AppCompatActivity {
    private String address;
    private String username;
    private SharedPreferences sharedPref;
    private EditText rent;
    private EditText tooltype;
    private RequestQueue requestQueue;
    private int UserID;
    private String rent1;
    private String tool1;
    private Button homebutton;
    private EditText description;
    private String description1;
    private SharedPreferences.Editor editor;
    private EditText startofday;
    private EditText endofday;

    private String start;
    private String end;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lender);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        address = sharedPref.getString("Address_name", null);
        username = sharedPref.getString("Users_username", null);
        rent = findViewById(R.id.rent);
        tooltype = findViewById(R.id.tool);
        description = findViewById(R.id.description_of_tools);
        startofday = findViewById(R.id.starttime);
        endofday = findViewById(R.id.endtime);
        homebutton = findViewById(R.id.homeButton);
        homebutton.setOnClickListener(v -> {
            rent1 = rent.getText().toString();
            tool1 = tooltype.getText().toString();
            description1 = description.getText().toString();
            start = startofday.getText().toString();
            end = endofday.getText().toString();
            gotohome("https://studev.groept.be/api/a24pt215/selectUserId/"+username);
        });

    }
    public void gotohome(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);

        // Make the GET request to retrieve community names the user is part of
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject communityObject = response.getJSONObject(0);

                            // Get the community name and community_id from the response
                            UserID = communityObject.getInt("user_id");

                        } catch (JSONException e) {
                            Log.e("Error", "Error processing JSON response", e);
                        }
                        insertLender("https://studev.groept.be/api/a24pt215/InsertLender",UserID, address, tool1, rent1, description1, start, end);
                        Intent i = new Intent(Lender.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Error fetching communities", error);
                    }
                });

        // Add the request to the request queue
        requestQueue.add(submitRequest);
    }
    public void insertLender(String url, int user_id, String address_val, String tooltype, String rent, String description, String startday, String endday) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("MessagePost", "Response: " + response);
                    Toast.makeText(Lender.this, "Message sent!", Toast.LENGTH_SHORT).show();


                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("VolleyError", "Error: " + errorMsg);
                        Toast.makeText(Lender.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("VolleyError", "Unknown error occurred");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("addressval", address_val);  // must match backend expected name
                params.put("toolval", tooltype);
                params.put("userval", String.valueOf(user_id));
                params.put("rentval",rent);
                params.put("descriptionval", description);
                params.put("startofday", startday);
                params.put("endofday", endday);
                return params;
            }
        };

        Volley.newRequestQueue(Lender.this).add(stringRequest);
    }
}