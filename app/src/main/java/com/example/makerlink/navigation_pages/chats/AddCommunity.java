package com.example.makerlink.navigation_pages.chats;

import static com.example.makerlink.R.*;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.makerlink.R;
import com.example.makerlink.utils.LocaleHelper;

public class AddCommunity extends AppCompatActivity {

    private ImageButton backbutton1;
    private Button join1;
    private Button create1;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getSavedLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_community);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backbutton1 = findViewById(R.id.backButton);
        join1 = findViewById(id.joinButton);
        create1 = findViewById(id.createButton);
        backbutton1.setOnClickListener(v -> {finish();});
        join1.setOnClickListener(v -> {
            Intent i = new Intent(this, JoinActivity.class);
            startActivity(i);
        });
        create1.setOnClickListener(v -> {
            Intent i1 = new Intent(this, CreateActivity.class);
            startActivity(i1);
        });
    }
}