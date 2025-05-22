package com.example.makerlink.navigation_pages.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.makerlink.MainActivity;
import com.example.makerlink.R;
import com.example.makerlink.utils.LocaleHelper;

public class ChangeLanguage extends AppCompatActivity {

    String[] languages = {"English", "Français", "Shqip", "Türkçe","Italiano","Dutch"};
    String[] languageCodes = {"en", "fr", "sq", "tr","it","nl"};
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getSavedLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        Button btnChangeLanguage = findViewById(R.id.btn_change_language); // Make sure you have this button in XML

        btnChangeLanguage.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Choose Language")
                    .setItems(languages, (dialog, which) -> {
                        // Save selection
                        getSharedPreferences("settings", MODE_PRIVATE)
                                .edit()
                                .putString("language", languageCodes[which])
                                .apply();

                        // Restart the app to apply locale change
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .show();
        });
    }
}