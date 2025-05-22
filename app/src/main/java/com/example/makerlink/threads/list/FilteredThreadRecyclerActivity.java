package com.example.makerlink.threads.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class FilteredThreadRecyclerActivity extends AppCompatActivity {

    private ArrayList<ThreadRecyclerModel> threadItems;
    private ThreadRecyclerViewAdapter threadAdapter;
    private TextView heading;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filtered_thread_recycler);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        searchView = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.my_recycler_filtered);
        heading = findViewById(R.id.headingRecyclerFiltered);

        threadItems = new ArrayList<>(); /// DONT FORGET TO ALWAYS INITIALIZE ARRAYLIST

        threadAdapter = new ThreadRecyclerViewAdapter(this, threadItems);
        recyclerView.setAdapter(threadAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK);  // Set the text color to black
            searchEditText.setHintTextColor(Color.BLACK);  // Set the hint color to black
        }
        // Change color of the magnifying glass icon to black
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) {
            searchIcon.setImageResource(R.drawable.icon_search);
            searchIcon.setImageTintList(null);
            searchIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);  // Change icon color to black
        }

        searchView.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (threadAdapter != null) {
                    threadAdapter.getFilter().filter(query);
                }
                return false; // We handle everything on text change
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (threadAdapter != null) {
                    threadAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpThread("https://studev.groept.be/api/a24pt215/RetrieveAllThreads");
    }

    public void setUpThread(String requestURL) {
        System.out.println("inside setUpThread");
        threadItems.clear();  // <-- THIS IS ESSENTIAL!
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        heading.setText("Discover ... \uD83D\uDE80");
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
                                String threadDocument = o.getString("document");
                                String b64Image = o.getString("image_resource");
                                threadItems.add(new ThreadRecyclerModel(id, nameThread, nameThreadShort, b64Image, creationDate, authorID, domainID, threadDocument));
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                                System.out.println("json array problem");
                            }
                        }
                        recyclerView.scrollToPosition(0);
                        threadAdapter.updateFullList();
                        threadAdapter.notifyDataSetChanged(); // Update the RecyclerView
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
}