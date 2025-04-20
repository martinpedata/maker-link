package com.example.makerlink.access;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.makerlink.MainActivity;
import com.example.makerlink.R;

public class SignUpActivity extends AppCompatActivity {
    private TextView signUpText;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        editor = sharedPref.edit();
    }
    public void goToHome(View view) {

        EditText pw = findViewById(R.id.password);
        String passwordInput = pw.getText().toString();
        EditText un = findViewById(R.id.username);
        String usernameInput = un.getText().toString();
        EditText em = findViewById(R.id.email);
        String emailInput = em.getText().toString();
        EditText age = findViewById(R.id.age);
        String ageInput = age.getText().toString();
        EditText name = findViewById(R.id.name);
        String nameInput = name.getText().toString();

        SignUpValidity validity = new SignUpValidity(ageInput,nameInput,emailInput,passwordInput,usernameInput);
        switch (validity.checkValidity()) {
            case 0:
                if (!usernameInput.isEmpty() && !passwordInput.isEmpty()) {
                    // Save the name in SharedPreferences
                    editor.putString("UserName", usernameInput).apply();

                    // Go to NavigationTemplate
                    Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(i);
                    finish(); // Prevent going back to the welcome screen
                }
            case 1:
                /// ...
            case 2:
                /// ...
            case 3:
                /// ...
            default:
                /// ...
        }
    }
}