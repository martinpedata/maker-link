package com.example.makerlink.ui.chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.makerlink.ui.exchange.ExchangeFragment;
import com.example.makerlink.ui.exchange.InfoPage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ImageButton imageButton;
    private RecyclerView recyclerView;
    private EditText editText;
    private FloatingActionButton sendButton;
    private List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private TextView nameofcommunity;
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
        imageButton = findViewById(R.id.backButton);
        imageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recyclerViewMessages);
        editText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        nameofcommunity = findViewById(R.id.textView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);
        sendButton.setOnClickListener(v -> {
            String msgText = editText.getText().toString().trim();
            if (!msgText.isEmpty()) {
                Message message = new Message("User", msgText);
                messages.add(message);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);
                editText.setText("");
            }
        });
        nameofcommunity.setText("User");
    }
}