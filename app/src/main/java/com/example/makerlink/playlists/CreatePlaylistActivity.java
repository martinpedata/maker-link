package com.example.makerlink.playlists;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.MainActivity;
import com.example.makerlink.R;
import com.example.makerlink.threads.ThreadActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//TODO: CREATE A BUTTON FOR THE PLAYLIST CREATION AND TEST
public class CreatePlaylistActivity extends AppCompatActivity {
    private EditText privacyInput;
    private int privacy = -1;  ///NOTE: 1 means it is private, 0 that it's public.
    private String playlistName;
    private int userID;
    private ImageView arrow;
    private CardView privacyBox;
    private PopupMenu popup;
    private boolean setClicked = false;
    private String[] privacySetting = new String[2];
    private SharedPreferences sharedPref;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_playlist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText nameInput = findViewById(R.id.editTextPlaylist);
        playlistName = nameInput.getText().toString();

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        userID = sharedPref.getInt("user_ID", -1);

        privacyInput = findViewById(R.id.privacyText);
        privacyBox = findViewById(R.id.privacyBox);
        arrow = findViewById(R.id.privacyArrow);

        privacySetting[0] = "Private";
        privacySetting[1] = "Public";
    }

    public void privacyButtonClicked(View caller) {

        popup = new PopupMenu(CreatePlaylistActivity.this, privacyBox);
        if (!setClicked) {

            setClicked = true;
            arrow.setImageResource(R.drawable.arrow_temu_down);

            popup.getMenu().add(0, 0, 0, privacySetting[0]);
            popup.getMenu().add(0, 1, 1, privacySetting[1]);

            popup.setOnMenuItemClickListener(item -> {
                String selectedPlaylist = item.getTitle().toString();

                if (selectedPlaylist.equals(privacySetting[0])) {
                    privacy = 1;
                }
                else {
                    privacy = 0;
                }
                return true;
            });
            popup.show();
        }
        else {
            setClicked = false;
            arrow.setImageResource(R.drawable.arrow_temu_right);
        }
    }

    public void createPlaylist(View view) {
        String requestURL = "https://studev.groept.be/api/a24pt215/InsertPlaylist/" + userID + "/" + privacy + "/" + playlistName;

        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest retrieveRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(CreatePlaylistActivity.this, "New playlist created ! ", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreatePlaylistActivity.this, "Unable to create playlist", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(retrieveRequest);
    }
}