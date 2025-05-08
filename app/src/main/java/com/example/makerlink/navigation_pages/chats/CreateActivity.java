package com.example.makerlink.navigation_pages.chats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.makerlink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateActivity extends AppCompatActivity{
    private ImageButton backbutton;
    private EditText domainname1;
    private EditText communityname1;
    private RequestQueue requestQueue;
    private List<String> DomainList;
    private String domain;
    private Integer domainid;
    private int USER_ID;
    private Button createbutton;
    private int communityid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backbutton = findViewById(R.id.backButton);
        backbutton.setOnClickListener(v -> {
            finish();
        });
        SharedPreferences sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        USER_ID = sharedPref.getInt("user_ID", -1);
        domainname1 = findViewById(R.id.domainname);
        communityname1 = findViewById(R.id.communityname);
        createbutton = findViewById(R.id.creationButton);
        createbutton.setOnClickListener(v -> {
            String communityname = communityname1.getText().toString().trim();
            getDomain("https://studev.groept.be/api/a24pt215/RetrieveAllDomains", new DomainIdCallback() {
                @Override
                public void onDomainIdReceived(int domainId) {
                    insertnewcommunity(communityname, domainId, () -> {
                        getcommunityid("https://studev.groept.be/api/a24pt215/selectlastcommunity/" + communityname + "/" + domainId,
                                new CommunityIdCallback() {
                                    @Override
                                    public void onCommunityIdReceived(int communityId) {
                                        saveUserCommunityAssociation(USER_ID, communityId);
                                    }
                                });
                    });
                }
            });
            finish();
        });
    }
    public void getDomain(String requestURL, DomainIdCallback callback) {
        requestQueue = Volley.newRequestQueue(this);
        DomainList = new ArrayList<>();
        domain = domainname1.getText().toString().trim();

        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                response -> {
                    boolean found = false;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject o = response.getJSONObject(i);
                            String domainsnames = o.getString("name");
                            DomainList.add(domainsnames);

                            if (domainsnames.toLowerCase().contains(domain.toLowerCase()) ||
                                    domain.toLowerCase().contains(domainsnames.toLowerCase())) {

                                found = true;
                                getdomainID("https://studev.groept.be/api/a24pt215/selectDomainfromname/" + domainsnames, callback);
                                break;
                            }
                        } catch (JSONException e) {
                            Log.e("JSON", "Parsing error: " + e.getMessage());
                        }
                    }

                    if (!found) {
                        insertdomain(domain);
                        getdomainID("https://studev.groept.be/api/a24pt215/selectDomainfromname/" + domain, callback);
                    }
                },
                error -> {
                    Log.e("Volley", "Error: " + error.toString());
                    Toast.makeText(this, "Error fetching domains", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(submitRequest);
    }
    public void getdomainID(String requestURL, DomainIdCallback callback){
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject o = response.getJSONObject(0);
                            int domainID = o.getInt("id");
                            domainid = domainID;
                            callback.onDomainIdReceived(domainID);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("ErrorFetchingDomains", "Volley error: " + error.getMessage());
                    Toast.makeText(CreateActivity.this, "Error fetching domain ID", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(submitRequest);
    }
    private void insertdomain(String namedomain) {
        String url = "https://studev.groept.be/api/a24pt215/InsertDomain";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("InsertDomain", "Response: " + response);
                    // Handle success (e.g., show a success message or update UI)
                    Log.d("InsertDomain", "Successfully inserted domain");
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("InsertDomain", "Error: " + errorMsg);
                        Toast.makeText(CreateActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("InsertDomain", "Unknown error occurred");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("domainnam", String.valueOf(namedomain));
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }
    private void insertnewcommunity(String community, int domainID, Runnable onSuccess) {
        String url = "https://studev.groept.be/api/a24pt215/Insertcommunityfromcreate";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("InsertCommunity", "Response: " + response);
                    Toast.makeText(this, "Community Inserted: " + response, Toast.LENGTH_SHORT).show();
                    onSuccess.run(); // Trigger next step only if insert succeeded
                },
                error -> {
                    String errorMsg = (error.networkResponse != null && error.networkResponse.data != null)
                            ? new String(error.networkResponse.data) : "Unknown error";
                    Log.e("InsertCommunity", "Error: " + errorMsg);
                    Toast.makeText(this, "InsertCommunity error: " + errorMsg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("communityname", community);
                params.put("domainid", String.valueOf(domainID));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
    private void getcommunityid(String requestURL, CommunityIdCallback callback){
        requestQueue = Volley.newRequestQueue(this);

        // Make the GET request to retrieve community names the user is part of
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                JSONObject communityObject = response.getJSONObject(0);
                                int communityId = communityObject.getInt("id");
                                communityid = communityId;
                                callback.onCommunityIdReceived(communityId);
                            }
                        } catch (JSONException e) {
                            Log.e("Error", "Error processing JSON response", e);
                        }
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
                        Toast.makeText(CreateActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
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