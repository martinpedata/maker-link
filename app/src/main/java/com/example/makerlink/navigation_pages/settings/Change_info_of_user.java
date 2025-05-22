package com.example.makerlink.navigation_pages.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Change_info_of_user extends AppCompatActivity {
    private TextInputEditText firstname;
    private TextInputEditText addressuser;
    private TextInputEditText lenderuser;
    private TextInputEditText changeusername;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private RequestQueue requestQueue;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_info_of_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        int user_id = sharedPref.getInt("user_ID", -1);
        firstname = findViewById(R.id.name_profile_fragment);
        addressuser = findViewById(R.id.address_profile_fragment);
        lenderuser = findViewById(R.id.lender_profile_fragment);
        changeusername = findViewById(R.id.username_change_profile);
        save = findViewById(R.id.savebutton);
        save.setOnClickListener(v -> {
            String name = firstname.getText().toString();
            String address = addressuser.getText().toString();
            String lender = lenderuser.getText().toString();
            String username = changeusername.getText().toString();
            updateUser(user_id, name,username,address,lender);
            setUpUser("https://studev.groept.be/api/a24pt215/retrieveuser/"+user_id);
            if(lender.trim().toLowerCase().equals("y")){
                checkUser("https://studev.groept.be/api/a24pt215/RetrieveAllUsers", user_id, exists -> {
                    if (exists) {
                        Log.d("CheckUser", "User exists!");
                        finish();
                    } else {
                        Log.d("CheckUser", "User does NOT exist.");
                        Intent i = new Intent(this, AddTool.class);
                        startActivity(i);
                        finish();

                    }
                });
            }
            else{
                deleteLender("https://studev.groept.be/api/a24pt215/DeleteLenderInfo", user_id,
                        response -> Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show(),
                        error -> Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
                );
                finish();
            }
        });
        setUpUser("https://studev.groept.be/api/a24pt215/retrieveuser/"+user_id);

    }
    public void setUpUser(String requestURL) {

        requestQueue = Volley.newRequestQueue(this);


        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                                JSONObject communityObject = response.getJSONObject(0);


                                String name = communityObject.getString("name");
                                String username = communityObject.getString("username");
                                String address = communityObject.getString("address");
                                String lender = communityObject.getString("lender");
                                firstname.setText(name);
                                addressuser.setText(address);
                                lenderuser.setText(lender);
                                changeusername.setText(username);

                        } catch (JSONException e) {
                            Log.e("Error", "Error processing JSON response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Error fetching user", error);
                    }
                });

        // Add the request to the request queue
        requestQueue.add(submitRequest);
    }
    private void updateUser(int userId, String name, String username, String address, String lender) {
        String url = "https://studev.groept.be/api/a24pt215/UpdateUserInfo";  // Replace with your actual API endpoint

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("UpdateUser", "Response: " + response);
                    // Handle success
                    Log.d("UpdateUser", "Successfully updated User");
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("UpdateUser", "Error: " + errorMsg);
                        Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("UpdateUser", "Unknown error occurred");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userval", String.valueOf(userId));
                params.put("usernam", username);
                params.put("nam", name);
                params.put("addres", address);
                params.put("lenderval", lender);
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }
    public void checkUser(String requestURL, int targetUserId, UserExistCallback callback) {
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest submitRequest = new JsonArrayRequest(
                Request.Method.GET,
                requestURL,
                null,
                response -> {
                    boolean found = false;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject user = response.getJSONObject(i);
                            int id = user.getInt("user_id");

                            if (id == targetUserId) {
                                found = true;
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onResult(found);
                },
                error -> {
                    Log.e("Error", "Error fetching user", error);
                    callback.onResult(false);
                }
        );

        requestQueue.add(submitRequest);
    }
    public void deleteLender(String url, int lenderId, final Response.Listener<String> successListener, final Response.ErrorListener errorListener) {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest deleteRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("DeleteLender", "Response: " + response);
                    successListener.onResponse(response);
                },
                error -> {
                    Log.e("DeleteLender", "Error: ", error);
                    errorListener.onErrorResponse(error);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userval", String.valueOf(lenderId));  // or whatever your API expects
                return params;
            }
        };

        requestQueue.add(deleteRequest);
    }
}