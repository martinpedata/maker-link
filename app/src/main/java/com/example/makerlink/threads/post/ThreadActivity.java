package com.example.makerlink.threads.post;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
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
import java.util.function.Consumer;

public class ThreadActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private String domainName;
    private List<String> playlistNames = new ArrayList<>();
    private List<Integer> playlistIDs = new ArrayList<>();
    private PopupMenu popup;
    private int threadID;
    private int domainID;
    private Bitmap bitmapImageThumbnail;
    private int authorID;
    private int userID;
    private int playlistClickedID;
    private String playlistClickedName;
    private ImageButton heart;
    private String userName;
    private RequestQueue requestQueue;
    private TextView domainText;
    private TextView dateText;
    private TextView authorText;
    private TextView nameText;
    private String threadName;
    private String date;
    private WebView embeddedLink;
    private String threadDocument;
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
        embeddedLink = findViewById(R.id.threadDocumentPlaceHolder);

        /// Retrieve info from RecyclerThreadAdapter class (from the extra info on intent)
        threadName = getIntent().getStringExtra("threadName");
        threadID = getIntent().getIntExtra("threadID", -1);
        authorID = getIntent().getIntExtra("threadAuthor", -1); //THe person who created the thread
        domainID = getIntent().getIntExtra("threadDomain", -1);
        date = getIntent().getStringExtra("threadDate");
//        String threadThumbnailBase64 = getIntent().getStringExtra("threadThumbnail");
//        bitmapImageThumbnail = base64ToBitMap(threadThumbnailBase64);
        threadDocument = getIntent().getStringExtra("threadDocument");

        /// Retrive playlist and user info from PlaylistRecyclerActivity class (from shared pref)
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        userID = sharedPref.getInt("user_ID", -1); // The person currently using the app

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

        /// Retrieve state of the post: is it already in a playlist or not? Merely used to Update isFavorite and thus the color of the heart.


        isInPlaylist("https://studev.groept.be/api/a24pt215/IsThreadInPlaylist/" + threadID);


        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistNames.clear(); // So that the dropdown menu does not stack up values
                playlistIDs.clear();

                popup = new PopupMenu(ThreadActivity.this, heart); // anchor to the heart icon, initialize outside of conditional block to make it work.

                if (!isFavorite) {
                    heart.setColorFilter(Color.parseColor("#E53935")); // Mark as favorite
                    isFavorite = true;

                    // Prevent fast double-clicks
                    heart.setEnabled(false);
                    System.out.println("author_ID = " + authorID);
                    // Database connection

                }
                else {
                    isFavorite = false;
                    heart.setColorFilter(Color.parseColor("#808080"));
                }
                populatePlaylistMenu("https://studev.groept.be/api/a24pt215/RetrievePlaylists/" + userID);
            }
        });

        /// Display document in embedded placeholder

        embeddedLink.getSettings().setJavaScriptEnabled(true);
        embeddedLink.setWebViewClient(new WebViewClient());
        embeddedLink.loadUrl(threadDocument);

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

    public void isInPlaylist(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject o = response.getJSONObject(0);
                            isFavorite = true;
                        } catch (JSONException e)  /// EXCEPTION RETURNED WHEN EMPTY JSON ARRAY.
                        {
                            isFavorite = false;
                        }

                        if (isFavorite) {
                            heart.setColorFilter(Color.parseColor("#E53935")); // Mark as favorite
                        }
                        else {
                            heart.setColorFilter(Color.parseColor("#808080"));
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
    public void populatePlaylistMenu(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        /// Populate ArrayList playlists (of user)
                        for (int i = 0; i<response.length();i++) {
                            try {
                                JSONObject o = response.getJSONObject(i);
                                String name = o.getString("name_playlist");
                                int id = o.getInt("id");
                                playlistNames.add(name);
                                playlistIDs.add(id);
                            }
                            catch (JSONException e) {
                                Log.e("Erroreeee", e.getLocalizedMessage());
                            }
                        }

                        System.out.println("size listNames: " + playlistIDs.size());

                        ///  Add to dropdown menu
                        for (int i = 0; i < playlistNames.size(); i++) {
                            popup.getMenu().add(0, i, i, playlistNames.get(i));
                        }

                        heart.setEnabled(true);

                        popup.setOnMenuItemClickListener(item -> {
                            String selectedPlaylist = item.getTitle().toString();
                            int index = 0;
                            for (int i = 0; i<playlistNames.size(); i++) {
                                if (playlistNames.get(i).equals(selectedPlaylist)) {
                                    index = i;
                                }
                            }
                            playlistClickedName = selectedPlaylist;
                            playlistClickedID = playlistIDs.get(index); //Because id of playlist will be found at the same index on the IDs list as the name on the names list.

                            /// if isFavorite is true and not false because we updated the isFavorite right before this call was executed.
                            if (isFavorite) {
                                addThreadToPlaylist("https://studev.groept.be/api/a24pt215/AddThreadToPlaylist"); // POST REQUEST, DO NOT PUT PARAMS HERE BUT RATHER IN MAP OF JSONREQUEST
                            }
                            else {
                                removeFromPlaylist("https://studev.groept.be/api/a24pt215/RemoveThreadFromPlaylist");
                            }
                            return true;
                        });
                        popup.show();


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

    public void addThreadToPlaylist(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest (Request.Method.POST, requestURL,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ThreadActivity.this, "Thread added to " + playlistClickedName, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ThreadActivity.this, "Failed to add thread to " + playlistClickedName, Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameters to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idplaylist", String.valueOf(playlistClickedID));
                params.put("idthread", String.valueOf(threadID)); ///THE NAME KEYS HAVE TO BE THE SAME AS THE ":val" IN THE API, NOT AS THE COLUMNS OF THE TABLE
                return params;
            }
        };
        requestQueue.add(submitRequest);
    }

    public void removeFromPlaylist(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest (Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ThreadActivity.this, "Thread removed from " + playlistClickedName, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ThreadActivity.this, "Failed to remove thread from " + playlistClickedName, Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameters to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idplaylist", String.valueOf(playlistClickedID));
                params.put("idthread", String.valueOf(threadID)); ///THE NAME KEYS HAVE TO BE THE SAME AS THE ":val" IN THE API, NOT AS THE COLUMNS OF THE TABLE
                return params;
            }
        };
        requestQueue.add(submitRequest);
    }

    public void openThreadDoc(View v) {
        Intent i = new Intent(ThreadActivity.this, ThreadDocumentActivity.class);
        i.putExtra("threadDocument", threadDocument);
        startActivity(i);
    }

    public Bitmap base64ToBitMap(String b64String){
        byte[] imageBytes = Base64.decode( b64String, Base64.DEFAULT );
        Bitmap bitmap = BitmapFactory.decodeByteArray( imageBytes, 0, imageBytes.length );
        return bitmap;
    }
}