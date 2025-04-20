package com.example.makerlink.access;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.makerlink.MainActivity;
import com.example.makerlink.R;

public class LoginActivity extends AppCompatActivity {
    private TextView signUpText;
    private CredentialsVerification cv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /**
         * Sign up if no account logic.
         * */

        signUpText = findViewById(R.id.signUpText);
        SpannableString spannableString = new SpannableString("Don't have an account? Sign Up");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Log.d("ClickTest", "Sign Up clicked!");
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                try {
                    ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.teal_200)); // Use direct color first
                    ds.setUnderlineText(true);
                } catch (Exception e) {
                    ds.setColor(Color.BLUE); // Fallback color
                }
            }
        };

        spannableString.setSpan(
                clickableSpan,
                spannableString.length() - 7,  // "Sign Up" starts at length-7
                spannableString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        signUpText.setText(spannableString);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void goToHome(View view) {

        EditText pw = findViewById(R.id.password);
        String passwordInput = pw.getText().toString();
        EditText un = findViewById(R.id.username);
        String usernameInput = pw.getText().toString();

        cv = new CredentialsVerification( usernameInput , passwordInput );

        if (cv.checkValidityOfLogin() == 1) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            // ... create a Text View pop up "invalid username or password"
        }
    }
}