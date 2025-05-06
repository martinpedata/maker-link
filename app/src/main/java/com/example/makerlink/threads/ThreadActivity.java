package com.example.makerlink.threads;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import kotlin.jvm.internal.Lambda;

public class ThreadActivity extends AppCompatActivity {

    private String domainName;
    private List<String> playlists = new ArrayList<>();
    private PopupMenu popup;
    private int domainID;
    private int authorID;
    private ImageButton heart;
    private String userName;
    private RequestQueue requestQueue;
    private TextView domainText;
    private TextView dateText;
    private TextView authorText;
    private TextView nameText;
    private String threadName;
    private String date;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thread);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nameText = findViewById(R.id.nameThread);
        domainText = findViewById(R.id.domainOfThread);
        dateText = findViewById(R.id.date);
        authorText = findViewById(R.id.authorItem);
        heart = findViewById(R.id.addToPlaylist);

        /// Retrieve values from RecyclerThread class
        threadName = getIntent().getStringExtra("threadName");
        authorID = getIntent().getIntExtra("threadAuthor", -1);
        domainID = getIntent().getIntExtra("threadDomain", -1);
        date = getIntent().getStringExtra("threadDate");


        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite) {
                    heart.setColorFilter(Color.parseColor("#E53935")); // Mark as favorite
                    isFavorite = true;

                    // Show popup menu
                    popup = new PopupMenu(ThreadActivity.this, heart); // anchor to the heart icon

                    // Dummy playlists
                    populateDropdownMenu();

                    // Handle click events
                    popup.setOnMenuItemClickListener(item -> {
                        String selectedPlaylist = item.getTitle().toString();
                        Toast.makeText(ThreadActivity.this, "Added to " + selectedPlaylist, Toast.LENGTH_SHORT).show();
                        // TODO: Add logic to store post in that playlist
                        return true;
                    });
                    popup.show();
                }
                else {
                    heart.setColorFilter(Color.parseColor("#808080"));
                    Toast.makeText(ThreadActivity.this, "Removed from playlist", Toast.LENGTH_SHORT).show();
                    isFavorite = false;
                }
            }
        });

        ///  Iterate database Twice with a callback method (Consumer<>)to ensure program waits for response before advancing.
        ///Lambda expression on " name -> " below.

        retrieveTextFromUrl("https://studev.groept.be/api/a24pt215/RetrieveDomainNameFromID/" + domainID, "name", name -> {
            domainName = name;

            //Program waits for domainName to be updated, thanks to callback method, before advancing to the next database query

            retrieveTextFromUrl("https://studev.groept.be/api/a24pt215/RetrieveUserNameFromID/" + authorID, "username", username -> {
                userName = username;

                //Once both names are retrieved, update UI.
                nameText.setText(threadName);
                authorText.setText(userName);
                dateText.setText(date);
                domainText.setText(domainName);
            });
        });
    }

    public void retrieveTextFromUrl (String requestURL, String key, Consumer<String> callback) {
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject o = response.getJSONObject(0);
                            String specificName = o.getString(key);
                            callback.accept(specificName);
                        } catch (JSONException e) {
                            Log.e("Erroreeee", e.getLocalizedMessage());
                        }
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
    public void populateDropdownMenu() {
        playlists.add("DIY");
        playlists.add("Yo momma");
        for (int i = 0; i < playlists.size(); i++) {
            popup.getMenu().add(0, i, i, playlists.get(i));
        }
    }
}