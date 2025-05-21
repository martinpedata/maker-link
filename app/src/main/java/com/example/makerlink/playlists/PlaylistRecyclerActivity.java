package com.example.makerlink.playlists;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.example.makerlink.threads.post.CreateThreadActivity;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistRecyclerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlaylistRecyclerAdapter playlistAdapter;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int user_ID;
    private TextView title;
    private LinearLayout linearLayout;
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
        linearLayout = findViewById(R.id.linearLayoutPlaylists);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        requestQueue = Volley.newRequestQueue(this);

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

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition(); // get swiped position
            int playlistid = playlistsItems.get(position).getPlaylistID(); // get ID of swiped thread

            Snackbar snackbar = Snackbar.make(linearLayout, "Thread Deleted!", Snackbar.LENGTH_LONG);
            snackbar.show();

            deletePlaylist("https://studev.groept.be/api/a24pt215/DeletePlaylist", playlistid);
            playlistsItems.remove(position);
            playlistAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;

                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setAntiAlias(true);

                // Calculate position of red circle
                float circleRadius = 60f;
                float circleCenterY = itemView.getTop() + (itemView.getHeight() / 2f);
                float circleCenterX;

                if (dX > 0) { // Swiping right
                    circleCenterX = itemView.getLeft() + 100;
                } else { // Swiping left
                    circleCenterX = itemView.getRight() - 100;
                }

                // Draw red circle
                c.drawCircle(circleCenterX, circleCenterY, circleRadius, paint);

                // Draw white bin icon
                Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.bin_icon); // use your icon name here
                if (icon != null) {
                    int iconSize = 60;
                    int left = (int) (circleCenterX - iconSize / 2);
                    int top = (int) (circleCenterY - iconSize / 2);
                    int right = (int) (circleCenterX + iconSize / 2);
                    int bottom = (int) (circleCenterY + iconSize / 2);
                    icon.setBounds(left, top, right, bottom);
                    icon.setTint(Color.WHITE);
                    icon.draw(c);
                }
            }
        }
    };

    public void deletePlaylist(String requestURL, int playlistid) {
        StringRequest submitRequest = new StringRequest (Request.Method.POST, requestURL,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Turn the progress widget off
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(playlistid));
                return params;
            }
        };

        requestQueue.add(submitRequest);
    }
    public void setUpPlaylists(String requestURL) {
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