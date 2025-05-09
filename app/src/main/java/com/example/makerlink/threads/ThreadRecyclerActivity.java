package com.example.makerlink.threads;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.example.makerlink.navigation_pages.chats.ChatActivity;
import com.example.makerlink.navigation_pages.chats.Community_Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ThreadRecyclerActivity extends AppCompatActivity {

    ArrayList<ThreadRecyclerModel> threadItems = new ArrayList<>();
    ArrayList<Integer> threads_in_pLaylist = new ArrayList<>();
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private int playlist_id;
    private TextView headingActivity;
    private ThreadRecyclerViewAdapter threadAdapter;
    private String heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thread_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.my_recycler);
        headingActivity = findViewById(R.id.headingRecycler);
        playlist_id = getIntent().getIntExtra("playlistID", -1);

        threadAdapter = new ThreadRecyclerViewAdapter(this, threadItems);
        recyclerView.setAdapter(threadAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }

    /// NEEDED BECAUSE WHEN CLICKING A THREAD, THIS CLASS IS NOT DESTROYED BUT PAUSED
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences("storeFilteredSearch", MODE_PRIVATE);
        int isFiltered = sharedPref.getInt("isFiltered", -1);

        System.out.println("isFiltered in onCreate: " + isFiltered);
        int counter = 0; //Checking if we're in a playlist or in a filtered search
        switch (isFiltered) {
            case 5:
                heading = getIntent().getStringExtra("playlistName");
                playlist_id = getIntent().getIntExtra("playlistID", -1);
                setUpThread("https://studev.groept.be/api/a24pt215/RetrieveContentPlaylists/"+playlist_id); //INSERT PLAYLIST ID
                counter = 1;
                break;
            case 4:
                setUpThread("https://studev.groept.be/api/a24pt215/RetrieveSomeThreads/" + 5); //INSERT DOMAIN ID
                break;
            case 3:
                setUpThread("https://studev.groept.be/api/a24pt215/RetrieveSomeThreads/" + 7); //INSERT DOMAIN ID
                break;
            case 2:
                setUpThread("https://studev.groept.be/api/a24pt215/RetrieveSomeThreads/" + 1); //INSERT DOMAIN ID
                break;
            case 1:
                setUpThread("https://studev.groept.be/api/a24pt215/RetrieveSomeThreads/" + 6); //INSERT DOMAIN ID
                break;
            case 0:
                setUpThread("https://studev.groept.be/api/a24pt215/RetrieveAllThreads"); //ALL THREADS
                break;
            }
        if (counter == 0) {
            heading = sharedPref.getString("nameDomain", null);
            }
    }

    public void setUpThread(String requestURL) {
        System.out.println("inside setUpThread");
        threadItems.clear();  // <-- THIS IS ESSENTIAL!
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("inside onResponse of setUpThread");
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject o = response.getJSONObject(i);

                                int id = o.getInt("id");
                                String nameThread = o.getString("name");

                                String nameThreadShort = "";
                                //Needed because we want the full name when we open the activity and short name when in scrolling mode.
                                if (nameThread.length() > 14) {
                                    nameThreadShort = nameThread.substring(0,12) + "...";
                                }
                                else {
                                    nameThreadShort = nameThread;
                                }

                                int authorID = o.getInt("author_id");
                                int domainID = o.getInt("domain_id");
                                String creationDate = o.getString("creationdate").substring(0,10);
                                String b64Image = o.getString("image_resource");

                                Bitmap bitmapImage = base64ToBitMap(b64Image); //The b64 is converted to Bitmap in the helper method below

                                threadItems.add(new ThreadRecyclerModel(id, nameThread, nameThreadShort, bitmapImage, creationDate, authorID, domainID));
                            }
                            catch (JSONException e) {
                                System.out.println("json array empty");
                            }
                        }
                        recyclerView.scrollToPosition(0);
                        threadAdapter.notifyDataSetChanged(); // Update the RecyclerView
                        headingActivity.setText(heading);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("inside onError");
                        Log.e("ErrorThreadCreazione", error.getLocalizedMessage());
                    }
                }
        );
        requestQueue.add(submitRequest);
    }
    /// Putting this in the OnBinding of the adapter class made the program slow because not only it continuously converted b64->Bitmap during scrolling,
    /// but also did this in the UI thread instead of background. In this way, the conversion is only done once (before creation of Model objects) and is done
    /// on background thread.
    public Bitmap base64ToBitMap(String b64String){
        byte[] imageBytes = Base64.decode( b64String, Base64.DEFAULT );
        Bitmap bitmap = BitmapFactory.decodeByteArray( imageBytes, 0, imageBytes.length );
        return bitmap;
    }
}