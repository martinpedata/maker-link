package com.example.makerlink.navigation_pages.discovery;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.example.makerlink.navigation_pages.chats.Chat;
import com.example.makerlink.navigation_pages.chats.ChatActivity;
import com.example.makerlink.navigation_pages.chats.Community_Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderList extends AppCompatActivity {
    private RecyclerView recyclerView1, recyclerView2;
    private List<Order> orderlist;
    private List<Order> orderlist1;
    private RequestQueue requestQueue;
    private OrderAdapter orderAdapter;
    private OrderAdapter lenderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int UserID = sharedPref.getInt("user_ID", -1);
        recyclerView1 = findViewById(R.id.recyclerView1);
        recyclerView2 = findViewById(R.id.recyclerView2);
        setUpOrders("https://studev.groept.be/api/a24pt215/SelectOrdersLender/"+UserID);
        setUpOrders2("https://studev.groept.be/api/a24pt215/SelectLenderorders/"+UserID);
    }
    public void setUpOrders(String requestURL) {
        if (orderlist == null) {
            orderlist = new ArrayList<>();
        } else {
            orderlist.clear();
        }
        requestQueue = Volley.newRequestQueue(this);

        // Make the GET request to retrieve community names the user is part of
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            // Iterate over the response array to get each community's data
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject communityObject = response.getJSONObject(i);

                                // Get the community name and community_id from the response
                                int id_order = communityObject.getInt("id_order");
                                String tool = communityObject.getString("tool");
                                int rent = communityObject.getInt("price");
                                String dateorder = communityObject.getString("date_of_order");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                Date dateorder1 = sdf.parse(dateorder);
                                Calendar calendar_dateorder = Calendar.getInstance();
                                calendar_dateorder.setTime(dateorder1);
                                String start = communityObject.getString("start_rent");
                                Date start1 = sdf.parse(start);
                                Calendar calendar_start = Calendar.getInstance();
                                calendar_start.setTime(start1);
                                String end = communityObject.getString("end_rent");
                                Date end1 = sdf.parse(end);
                                Calendar calendar_end = Calendar.getInstance();
                                calendar_end.setTime(end1);


                                // Add the community to the chatList
                                orderlist.add(new Order(id_order, calendar_start,calendar_end,calendar_dateorder, rent, tool));
                            }

                            // Now, set the adapter with the list of communities
                            if (orderAdapter == null) {
                                // First-time setup of the adapter
                                orderAdapter = new OrderAdapter(orderlist);

                                // Set up RecyclerView with the adapter and layout manager
                                recyclerView2.setAdapter(orderAdapter);
                                recyclerView2.setLayoutManager(new LinearLayoutManager(OrderList.this));
                            } else {
                                recyclerView2.scrollToPosition(0);
                                orderAdapter.notifyDataSetChanged(); // Update the RecyclerView
                            }

                        } catch (JSONException e) {
                            Log.e("Error", "Error processing JSON response", e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
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
    public void setUpOrders2(String requestURL) {
        if (orderlist1 == null) {
            orderlist1 = new ArrayList<>();
        } else {
            orderlist1.clear();
        }
        requestQueue = Volley.newRequestQueue(this);

        // Make the GET request to retrieve community names the user is part of
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            // Iterate over the response array to get each community's data
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject communityObject = response.getJSONObject(i);

                                // Get the community name and community_id from the response
                                int id_order = communityObject.getInt("id_order");
                                String tool = communityObject.getString("tool");
                                int rent = communityObject.getInt("price");
                                String dateorder = communityObject.getString("date_of_order");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                Date dateorder1 = sdf.parse(dateorder);
                                Calendar calendar_dateorder = Calendar.getInstance();
                                calendar_dateorder.setTime(dateorder1);
                                String start = communityObject.getString("start_rent");
                                Date start1 = sdf.parse(start);
                                Calendar calendar_start = Calendar.getInstance();
                                calendar_start.setTime(start1);
                                String end = communityObject.getString("end_rent");
                                Date end1 = sdf.parse(end);
                                Calendar calendar_end = Calendar.getInstance();
                                calendar_end.setTime(end1);


                                // Add the community to the chatList
                                orderlist1.add(new Order(id_order, calendar_start,calendar_end,calendar_dateorder, rent, tool));
                            }

                            // Now, set the adapter with the list of communities
                            if (lenderAdapter == null) {
                                // First-time setup of the adapter
                                lenderAdapter = new OrderAdapter(orderlist1);

                                // Set up RecyclerView with the adapter and layout manager
                                recyclerView1.setAdapter(lenderAdapter);
                                recyclerView1.setLayoutManager(new LinearLayoutManager(OrderList.this));
                            } else {
                                recyclerView1.scrollToPosition(0);
                                lenderAdapter.notifyDataSetChanged(); // Update the RecyclerView
                            }

                        } catch (JSONException e) {
                            Log.e("Error", "Error processing JSON response", e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
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