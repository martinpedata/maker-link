package com.example.makerlink.threads.list;

import android.app.ProgressDialog;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserThreadRecyclerActivity extends AppCompatActivity {
    private FloatingActionButton createThreadButton;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private TextView heading;
    private int userID;
    private ArrayList<ThreadRecyclerModel> threadItems;
    private RequestQueue requestQueue;

    private ThreadRecyclerViewAdapter threadAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_thread_recycler);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        threadItems = new ArrayList<>();

        heading = findViewById(R.id.headingRecyclerUser);
        createThreadButton = findViewById(R.id.addThread);
        recyclerView = findViewById(R.id.my_recycler_userThreads);
        linearLayout = findViewById(R.id.linearLayout);

        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        userID = sharedPreferences.getInt("user_ID", -1);

        createThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserThreadRecyclerActivity.this, CreateThreadActivity.class);
                startActivity(i);
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        requestQueue = Volley.newRequestQueue(this);

        threadAdapter = new ThreadRecyclerViewAdapter(this, threadItems);
        recyclerView.setAdapter(threadAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpThread("https://studev.groept.be/api/a24pt215/RetrieveUserThreads/" + userID);
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition(); // get swiped position
            int threadId = threadItems.get(position).getID(); // get ID of swiped thread

            Snackbar snackbar = Snackbar.make(linearLayout, "Thread Deleted!", Snackbar.LENGTH_LONG);
            snackbar.show();

            deleteThread("https://studev.groept.be/api/a24pt215/DeleteThread", threadId);
            threadItems.remove(position);
            threadAdapter.notifyDataSetChanged();
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

    public void deleteThread(String requestURL, int threadId) {


        StringRequest submitRequest = new StringRequest (Request.Method.POST, requestURL,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(threadId));
                return params;
            }
        };

        requestQueue.add(submitRequest);
    }

    public void setUpThread(String requestURL) {
        System.out.println("inside setUpThread");
        threadItems.clear();  // <-- THIS IS ESSENTIAL!
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        heading.setText("Your Threads");
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