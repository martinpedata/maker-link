package com.example.makerlink.navigation_pages.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.makerlink.MainActivity;
import com.example.makerlink.R;
import com.example.makerlink.utils.LocaleHelper;

public class ChangeLanguage extends AppCompatActivity {

    String[] languages = {"English", "Français", "Shqip", "Türkçe", "Italian", "Dutch", "German", "Russian", "Greek", "Arabic"};
    String[] languageCodes = {"en", "fr", "sq", "tr", "it", "nl", "de", "ru", "el", "ar"};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getSavedLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        ListView listView = findViewById(R.id.language_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, languages) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.parseColor("#000000")); // Or use getResources().getColor(R.color.my_color)
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Save the selected language code
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putString("language", languageCodes[position])
                    .apply();

            // Restart the app to apply locale change
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}