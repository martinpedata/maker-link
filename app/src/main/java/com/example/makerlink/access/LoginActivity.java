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
import android.widget.Toast;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.MainActivity;
import com.example.makerlink.R;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private TextView signUpText;
    private String nameOfUser;
    private EditText pw;
    private EditText un;
    private String usernameInput;
    private int user_ID;
    private String passwordInput;
    private String usernameDB;
    private String passwordDB;

    private RequestQueue requestQueue;
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

        pw = findViewById(R.id.password);

        un = findViewById(R.id.username);
        usernameInput = un.getText().toString();

        checkValidityOfLogin("https://studev.groept.be/api/a24pt215/AllUserInfo/" + usernameInput);
    }

    public String hashPassWord(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPW = password + salt;
            byte[] hash = digest.digest(saltedPW.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    /// Retrieve database info
    public void checkValidityOfLogin(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ///Always execute in onResponse (because it is running in background thread).
                            JSONObject o = response.getJSONObject(0);
                            passwordDB = o.getString("password"); /// Retrieve salted hashed PW

                            String salt = o.getString("salt");
                            passwordInput = hashPassWord(pw.getText().toString(), salt); ///RE-HASH INPUTTED PASSWORD WITH THE SALT FROM DATABASE, COMPARE IT TO THE PASSWORD STORED IN THE DATABASE.

                            if (passwordInput.equals(passwordDB)) {
                                nameOfUser = o.getString("name");
                                user_ID = o.getInt("user_id");
                                System.out.println("user id before playlist: " + user_ID);
                                editor.putString("Name", nameOfUser).apply();
                                editor.putInt("user_ID", user_ID).apply();

                                FirebaseMessaging.getInstance().deleteToken() /// Go to NavigationTemplate
                                        .addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                // Generate new token
                                                FirebaseMessaging.getInstance().getToken()
                                                        .addOnCompleteListener(tokenTask -> {
                                                            if (tokenTask.isSuccessful()) {
                                                                String newToken = tokenTask.getResult();
                                                                Log.d("FCM", "New token: " + newToken);

                                                                // Send token to server
                                                                String updateTokenUrl = "https://studev.groept.be/api/a24pt215/updateFCMToken/" + newToken + "/" + user_ID;
                                                                StringRequest tokenRequest = new StringRequest(Request.Method.GET, updateTokenUrl,
                                                                        response1 -> {
                                                                            Log.d("FCM", "Token updated on server");

                                                                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                                                            startActivity(i);
                                                                            finish();
                                                                        },
                                                                        error -> {
                                                                            Log.e("FCM", "Failed to update token: " + error.toString());
                                                                            Toast.makeText(LoginActivity.this, "Error updating token", Toast.LENGTH_SHORT).show();
                                                                        });
                                                                requestQueue.add(tokenRequest);
                                                            } else {
                                                                Log.w("FCM", "Token generation failed", tokenTask.getException());
                                                            }
                                                        });
                                            } else {
                                                Log.w("FCM", "Token deletion failed", deleteTask.getException());
                                            }
                                        }); /// Prevent going back to the welcome screen
                            }
                            else {
                                pw.setText("");
                                pw.setHint("Invalid Password!");
                                pw.setHintTextColor(Color.RED);

                                // Remove focus to show hints
                                pw.clearFocus();
                                un.clearFocus();
                                pw.setError("");
                                un.setError("");
                            }
                        } catch (JSONException e) {
                            un.setText("");
                            pw.setText("");
                            pw.setHint("Invalid Credentials!");
                            un.setHint("Invalid Credentials!");

                            un.setHintTextColor(Color.RED);

                            // Remove focus to show hints
                            pw.clearFocus();
                            un.clearFocus();
                            pw.setError("");
                            un.setError("");
                            Log.e("Database", e.getMessage(), e);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Network error! Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("Errorrrrr", error.toString());
                    }
                }
        );
        requestQueue.add(submitRequest);
    }
}