package com.example.makerlink.access;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.MainActivity;
import com.example.makerlink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private TextView signUpText;
    private String nameOfUser;
    private RequestQueue requestQueue;
    private LoginCredentialsVerification cv;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

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

        ///Creating the sharedPref to store the Name of the user.

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        editor = sharedPref.edit();

        //sharedPref.edit().clear().apply();

        String savedName = sharedPref.getString("Name", null);//this

        /// If name already exists => user already logged in => skip welcome page

        if (savedName != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        ///Logic for the "create account" sentence

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
                spannableString.length() - 7,  /// "Sign Up" starts at length-7
                spannableString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        signUpText.setText(spannableString);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());

    }

    ///  OnClick method
    public void goToHome(View view) {

        EditText pw = findViewById(R.id.password);
        String passwordInput = pw.getText().toString();
        EditText un = findViewById(R.id.username);
        String usernameInput = un.getText().toString();

        cv = new LoginCredentialsVerification( usernameInput , passwordInput );

        if (cv.checkValidityOfLogin() == 1) {
            if (!usernameInput.isEmpty() && !passwordInput.isEmpty()) {
                // Save the name in SharedPreferences
                retrieveName("https://studev.groept.be/api/a24pt215/AllUserInfo/" + usernameInput);
            }
        }
        else {
            pw.setText("");
            un.setText("");

            pw.setHint("Invalid Credentials!");
            pw.setHintTextColor(Color.RED);

            un.setHint("Invalid Credentials!");
            un.setHintTextColor(Color.RED);

            // Remove focus to show hints
            pw.clearFocus();
            un.clearFocus();
            pw.setError("");
            un.setError("");
        }
    }

    /// Retrieve database info
    public void retrieveName(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ///Add the name to the welcome screen on the sharedPref.editor

                            /// YOU HAVE TO PUT THE RELEVANT CODE YOU WANT TO EXECUTE AFTER THE DATABASE IS QUERIED INSIDE HE ONRESPONSE !!!

                            JSONObject o = response.getJSONObject(0);
                            nameOfUser = o.getString("name");
                            editor.putString("Name", nameOfUser).apply();

                            /// Go to NavigationTemplate

                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish(); /// Prevent going back to the welcome screen
                        } catch (JSONException e) {
                            Log.e("Database", e.getMessage(), e);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        requestQueue.add(submitRequest);
    }
}