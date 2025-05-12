package com.example.makerlink.playlists;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.example.makerlink.threads.ThreadRecyclerActivity;
import com.example.makerlink.threads.ThreadRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaylistRecyclerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlaylistRecyclerAdapter playlistAdapter;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int user_ID;
    private TextView title;
    private ArrayList<PlaylistRecyclerModel> playlistsItems = new ArrayList<>();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlist_recycler);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainPlaylist), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.my_recycler_playlist);
        title = findViewById(R.id.headingRecyclerPlaylist);

        /// DEFINE ADAPTER IN ONCREATE, AND CREATE A FIELD ATTRIBUTE SUCH THAT ALL YOU HAVE TO DO IN THE METHOD BELOW IS UPDATE EXISTING ADAPTER
        playlistAdapter = new PlaylistRecyclerAdapter(PlaylistRecyclerActivity.this, playlistsItems);
        recyclerView.setAdapter(playlistAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(PlaylistRecyclerActivity.this));
    }

    /// Logic of onCreate is put on OnREsume for the same reason stated in ThreadRecyclerActivity: We want to update immediately.
    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        editor = sharedPref.edit();

        /// Retrieve user_ID from LoginPage sharedpref
        user_ID = sharedPref.getInt("user_ID", -1);

        System.out.println("user ID" + user_ID);

        setUpPlaylists("https://studev.groept.be/api/a24pt215/RetrievePlaylists/" + user_ID);
    }
    public void setUpPlaylists(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        playlistsItems.clear();
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject o = response.getJSONObject(i);

                                int playlist_id = o.getInt("id");

                                String namePlaylist = o.getString("name_playlist");

                                int owner_id = o.getInt("author_id");
                                int privacy = o.getInt("privacy");
                                playlistsItems.add(new PlaylistRecyclerModel(playlist_id,privacy,namePlaylist,owner_id));
                            }
                            catch (JSONException e) {
                                System.out.println("error iterating json array");
                            }

                        }
                        title.setText("Your Playlists");
                        recyclerView.scrollToPosition(0);
                        playlistAdapter.notifyDataSetChanged(); // Update the RecyclerView
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

    public void createPlaylistButton(View v) {
        Intent i = new Intent(PlaylistRecyclerActivity.this, CreatePlaylistActivity.class);
        startActivity(i);
    }
}