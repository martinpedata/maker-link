package com.example.makerlink.ui.chats;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private ImageButton imageButton;
    private RecyclerView recyclerView;
    private EditText editText;
    private FloatingActionButton sendButton;
    private List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private TextView nameofcommunity;
    private String chatName;
    private String User = "user"; // Replace with actual user if needed
    private int chatId;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatName = getIntent().getStringExtra("chatName");
        chatId = getIntent().getIntExtra("chat_id", -1);

        imageButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.recyclerViewMessages);
        editText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        nameofcommunity = findViewById(R.id.textView);

        if (nameofcommunity != null && chatName != null) {
            nameofcommunity.setText(chatName);
        }

        imageButton.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);

        // Fetch messages when the activity starts
        String url = "https://studev.groept.be/api/a24pt215/getMessage/" + chatId;
        setUpMessages(url);

        sendButton.setOnClickListener(v -> {
            String msgText = editText.getText().toString().trim();
            if (!msgText.isEmpty()) {
                postMessage("https://studev.groept.be/api/a24pt215/InsertMessage", User, msgText);
                editText.setText("");
            }
        });
    }

    // Function to fetch messages using Volley
    public void setUpMessages(String requestURL) {
        // Create a new request queue
        requestQueue = Volley.newRequestQueue(this);

        // Create the request to fetch the messages in JSON format
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Log for debugging
                        System.out.println("Inside onResponse of setUpMessages");

                        // Clear the existing messages
                        messages.clear();

                        // Iterate through the JSON array of messages
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                // Get each message JSON object
                                JSONObject o = response.getJSONObject(i);

                                // Extract message data from the JSON object
                                String sender = o.getString("author_of_message");
                                String messageContent = o.getString("message_content");

                                // Create a Message object and add it to the list
                                messages.add(new Message(sender, messageContent));
                            } catch (JSONException e) {
                                // Handle the JSON parsing error
                                System.out.println("Error iterating JSON array: " + e.getMessage());
                            }
                        }

                        // Update the RecyclerView with the list of messages
                        MessageAdapter messageAdapter = new MessageAdapter(messages);
                        recyclerView.setAdapter(messageAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

                        // Notify the adapter that the data set has changed
                        messageAdapter.notifyDataSetChanged();

                        // Scroll to the latest message
                        recyclerView.scrollToPosition(messages.size() - 1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Check if error.getLocalizedMessage() is null and provide a default message
                        String errorMessage = (error.getLocalizedMessage() != null) ? error.getLocalizedMessage() : "Unknown error occurred";
                        Log.e("ErrorFetchingMessages", errorMessage);

                        // Optionally, you can also print the full error stack trace for debugging
                        error.printStackTrace();

                        // Show a toast message to the user
                        Toast.makeText(ChatActivity.this, "Error fetching messages", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the Volley request queue
        requestQueue.add(submitRequest);
    }

    public void postMessage(String url, String user, String message_txt) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("MessagePost", "Response: " + response);
                    Toast.makeText(ChatActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
                    setUpMessages("https://studev.groept.be/api/a24pt215/getMessage/" + chatId);

                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("VolleyError", "Error: " + errorMsg);
                        Toast.makeText(ChatActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("VolleyError", "Unknown error occurred");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("chatid", String.valueOf(chatId));  // must match backend expected name
                params.put("user", user);
                params.put("message", message_txt);
                return params;
            }
        };

        Volley.newRequestQueue(ChatActivity.this).add(stringRequest);
    }
}