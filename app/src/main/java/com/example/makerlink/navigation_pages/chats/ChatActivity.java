package com.example.makerlink.navigation_pages.chats;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.makerlink.FirebaseAuthenticator;
import com.example.makerlink.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private String User; // Replace with actual user if needed
    private int chatId;
    private RequestQueue requestQueue;
    private ImageView image;
    private Bitmap bitmap;
    private static final long POLL_INTERVAL = 5000;

    // Handler for periodic updates
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        startPollingForMessages();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "chat_channel", // must match builder channelId
                    "General Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        chatName = getIntent().getStringExtra("chatName");
        chatId = getIntent().getIntExtra("chat_id", -1);
        String image64 = getIntent().getStringExtra("imagePath");
        SharedPreferences sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        User = sharedPref.getString("UserName", null);

        imageButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.recyclerViewMessages);
        editText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        nameofcommunity = findViewById(R.id.textView);
        image = findViewById(R.id.imageView);
        bitmap = BitmapFactory.decodeFile(image64);
        image.setImageBitmap(bitmap);

        if (nameofcommunity != null && chatName != null) {
            nameofcommunity.setText(chatName);
        }

        imageButton.setOnClickListener(v -> {
            ispresent(chatId);
            finish();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messages, User);
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
    private void startPollingForMessages() {
        // This will periodically fetch new messages from the server
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Fetch new messages from the server using the chatId
                setUpMessages("https://studev.groept.be/api/a24pt215/getMessage/" + chatId);

                // Re-run the polling after the specified interval (POLL_INTERVAL)
                handler.postDelayed(this, POLL_INTERVAL);
            }
        }, POLL_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
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
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messages.size() - 1);

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
                    getAbsentUserTokensAndNotify(chatId, user, message_txt);
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
    public void ispresent(int chatId) {
        // Assuming you have user_id stored in shared preferences or passed to the adapter
        SharedPreferences sharedPreferences = ChatActivity.this.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_ID", -1); // Fetch user_id from shared preferences

        // Check if user_id is valid
        if (userId == -1) {
            Log.e("Community_Adapter", "User ID is missing.");
            return;
        }

        // Construct the URL for your backend API
        String url = "https://studev.groept.be/api/a24pt215/NotPresentInChat";  // Replace with actual URL

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("MessagePost", "Response: " + response);
                    // Handle successful presence update
                    Toast.makeText(ChatActivity.this, "You are now marked as not present in the chat!", Toast.LENGTH_SHORT).show();
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
                params.put("userval", String.valueOf(userId));  // Send user_id
                params.put("chatval", String.valueOf(chatId));  // Send chat_id
                return params;
            }
        };

        Volley.newRequestQueue(ChatActivity.this).add(stringRequest);
    }
    public void sendFCMNotification(List<String> fcmTokens, String title, String message) {
        new Thread(() -> {
            try {
                String accessToken = new FirebaseAuthenticator().getAccessToken();

                for (String token : fcmTokens) {
                    URL url = new URL("https://fcm.googleapis.com/v1/projects/makerlink-39d19/messages:send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                    conn.setRequestProperty("Content-Type", "application/json; UTF-8");

                    // Build the JSON body
                    JSONObject json = new JSONObject();
                    JSONObject notificationContent = new JSONObject();
                    notificationContent.put("title", title);
                    notificationContent.put("body", message);

                    JSONObject messageContent = new JSONObject();
                    messageContent.put("token", token);
                    messageContent.put("notification", notificationContent);

// Add channel_id to ensure it works on Android 8+
                    JSONObject androidConfig = new JSONObject();
                    androidConfig.put("priority", "high");

                    JSONObject androidNotification = new JSONObject();
                    androidNotification.put("channel_id", "chat_channel");

                    androidConfig.put("notification", androidNotification);
                    messageContent.put("android", androidConfig);

                    json.put("message", messageContent);

                    // Write request body
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = json.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Read response
                    int responseCode = conn.getResponseCode();
                    InputStream is = (responseCode < HttpURLConnection.HTTP_BAD_REQUEST)
                            ? conn.getInputStream()
                            : conn.getErrorStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    br.close();

                    Log.d("FCM_RESPONSE", "Token: " + token + " | Code: " + responseCode + " | Response: " + response);
                }

            } catch (Exception e) {
                Log.e("FCM_ERROR", "Error sending FCM", e);
            }
        }).start(); // Run in background thread to avoid NetworkOnMainThreadException
    }
    public void getAbsentUserTokensAndNotify(int chatId, String sender, String message) {
        String url = "https://studev.groept.be/api/a24pt215/getAbsentUserTokens/" + chatId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> tokens = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String token = obj.getString("fcm_token");
                            tokens.add(token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // 🔄 Now run the notification sending in a background thread
                    new Thread(() -> {
                        try {
                            sendFCMNotification(tokens, sender + " sent a message", message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("FCM_SEND", "Failed to send notification: " + e.getMessage());
                        }
                    }).start();

                },
                error -> {
                    Log.e("FCM_TOKENS", "Failed to get tokens", error);
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
    private boolean checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001);
                return false;
            }
        }
        return true;
    }
}