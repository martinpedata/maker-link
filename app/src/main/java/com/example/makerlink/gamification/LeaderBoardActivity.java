package com.example.makerlink.gamification;

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
import com.example.makerlink.playlists.PlaylistRecyclerActivity;
import com.example.makerlink.playlists.PlaylistRecyclerAdapter;
import com.example.makerlink.playlists.PlaylistRecyclerModel;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderBoardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LeaderBoardAdapter leaderboardAdapter;
    private Integer badgeImage;
    private ArrayList<String> userNames;
    private ArrayList<Integer> pointsUsers;
    private ArrayList<LeaderBoardModel> users;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leader_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerLeaderboard);

        /// ARRAYLIST HAS TO BE INITIALIZED BEFORE BEING PASSED TO THE ADAPTER
        users = new ArrayList<>();
        userNames = new ArrayList<>();
        pointsUsers = new ArrayList<>();

        leaderboardAdapter = new LeaderBoardAdapter(LeaderBoardActivity.this, users);
        recyclerView.setAdapter(leaderboardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(LeaderBoardActivity.this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpLeaderboard("https://studev.groept.be/api/a24pt215/OrderUsersByPoints");
    }

    public void setUpLeaderboard(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        users.clear();
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject o = response.getJSONObject(i);

                                int points = o.getInt("points");
                                pointsUsers.add(points);
                                String username = o.getString("username");
                                userNames.add(username);
                            }
                            catch (JSONException e) {
                                System.out.println("error iterating json array");
                            }
                        }

                        for (int i = 0; i < userNames.size(); i++) {
                            int points = pointsUsers.get(i);
                            String user = userNames.get(i);
                            if (points > 3000){
                                badgeImage = R.drawable.level4;
                            }
                            else if (points > 2000) {
                                badgeImage = R.drawable.level3;
                            }
                            else if (points > 1000) {
                                badgeImage = R.drawable.level2;
                            }
                            else if (points > 500) {
                                badgeImage = R.drawable.level1;
                            }
                            else {
                                badgeImage = null;
                            }
                            users.add(new LeaderBoardModel(pointsUsers.get(i), badgeImage, i+1, user));
                        }

                        leaderboardAdapter.notifyDataSetChanged(); // Update the RecyclerView


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ErrorPlaylistCreazione", error.getLocalizedMessage());
                    }
                }
        );
        requestQueue.add(submitRequest);
    }
}