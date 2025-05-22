package com.example.makerlink.navigation_pages.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Change_info_lender extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Lender_info> lenderList;
    private RequestQueue requestQueue;
    private LenderAdapter adapter;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_info_lender);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerView_Lender);
        recyclerView.setLayoutManager(new LinearLayoutManager(Change_info_lender.this));

    }
    @Override
    public void onResume() {
        super.onResume();
        sharedPref = this.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int own_id = sharedPref.getInt("user_ID", -1);
        setUpLenders("https://studev.groept.be/api/a24pt215/RetrieveOwnLenderInfo/"+own_id);
    }
    public void setUpLenders(String requestURL) {
        if (lenderList == null) {
            lenderList = new ArrayList<>();
        } else {
            lenderList.clear();
        }
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            // Iterate over the response array to get each community's data
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject communityObject = response.getJSONObject(i);

                                int location_id = communityObject.getInt("id_location");
                                String name = communityObject.getString("name");
                                String address = communityObject.getString("address");
                                String tool = communityObject.getString("tooltype");
                                int rent = communityObject.getInt("rent");
                                String description = communityObject.getString("description");
                                int startofday = communityObject.getInt("start_time");
                                int endofday = communityObject.getInt("end_time");
                                String image = communityObject.getString("image_res");

                                lenderList.add(new Lender_info(location_id ,name ,address, rent, tool, description,  startofday,  endofday, image));
                            }

                            if (adapter == null) {
                                // First-time setup of the adapter
                                adapter = new LenderAdapter(lenderList);

                                // Set up RecyclerView with the adapter and layout manager
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(Change_info_lender.this));
                            } else {
                                recyclerView.scrollToPosition(0);
                                adapter.notifyDataSetChanged();
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
}