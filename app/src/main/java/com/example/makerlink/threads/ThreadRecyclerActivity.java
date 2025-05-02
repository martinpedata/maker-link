package com.example.makerlink.threads;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.makerlink.MainActivity;
import com.example.makerlink.R;
import com.example.makerlink.access.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ThreadRecyclerActivity extends AppCompatActivity {

    ArrayList<ThreadRecyclerModel> threadItems = new ArrayList<>();
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;

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

        SharedPreferences sharedPref = getSharedPreferences("storeFilteredSearch", MODE_PRIVATE);

        int isFiltered = sharedPref.getInt("isFiltered", -1);

        System.out.println("isFiltered is: " + isFiltered);
        switch (isFiltered) {
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
    }

    public void setUpThread(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("inside onResponse of signUpThread");
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                System.out.println("inside json array");
                                JSONObject o = response.getJSONObject(i);

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
                                threadItems.add(new ThreadRecyclerModel(nameThread, nameThreadShort, R.drawable.letter_c, creationDate, authorID, domainID));
                            }
                            catch (JSONException e) {
                                System.out.println("error iterating json array");
                            }

                        }
                        ThreadRecyclerViewAdapter threadAdapter = new ThreadRecyclerViewAdapter(ThreadRecyclerActivity.this, threadItems);
                        recyclerView.setAdapter(threadAdapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(ThreadRecyclerActivity.this,2));
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
}