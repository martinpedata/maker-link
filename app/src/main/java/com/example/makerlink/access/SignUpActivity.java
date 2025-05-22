package com.example.makerlink.access;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.MainActivity;
import com.example.makerlink.R;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SignUpActivity extends AppCompatActivity {
    private TextView signUpText;
    private SharedPreferences sharedPref;
    private RequestQueue requestQueue;
    private SharedPreferences.Editor editor;
    private EditText pw;
    private String passwordInput;
    private String salt;
    private EditText un;
    private String usernameInput;
    private EditText ld;
    private String lenderInput;
    private EditText age;
    private String ageInput;
    private EditText name;
    private String nameInput;
    private EditText addr;
    private String addressInput;
    private String[] credentials = new String[7];
    private int result;
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
        result = 0;
    }

    public void goToHome(View view) throws UnsupportedEncodingException {

        ///MAKE SURE THE PASSWORD IS HASHED.
        pw = findViewById(R.id.password);
        salt = HashCredentials.generateSalt(); //Random string to put in front of hashed pw (used to keep common passwords safe from rainbow tables).
        passwordInput = HashCredentials.hashPassWord(pw.getText().toString(), salt);  //Produce a salted hashed pw

        un = findViewById(R.id.username);
        usernameInput = un.getText().toString().trim();
        editor.putString("UserName", usernameInput).apply();

        ld = findViewById(R.id.lender);
        lenderInput = ld.getText().toString();

        age = findViewById(R.id.age);
        ageInput = age.getText().toString();

        name = findViewById(R.id.name);
        nameInput = name.getText().toString();

        addr = findViewById(R.id.address);
        addressInput = addr.getText().toString();

        credentials[0] = usernameInput;
        credentials[1] = nameInput;
        credentials[2] = ageInput;
        credentials[3] = addressInput;
        credentials[4] = lenderInput;
        credentials[5] = passwordInput;
        credentials[6] = salt;

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", "FCM Token: " + token);

                    // Now that we have the token, check the validity of the username
                    checkForValidity("https://studev.groept.be/api/a24pt215/AllUserInfo/" + usernameInput, token);
                });
    }

    public void checkForValidity(String requestURL, String token) {
        System.out.println("inside validity");
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject o = response.getJSONObject(0);
                            un.setText("");
                            un.setHint("Username taken");
                            un.setTextColor(Color.RED);
                            un.setHintTextColor(Color.RED);
                            name.setError("");
                            age.setError("");
                            addr.setError("");
                            ld.setError("");
                            pw.setError("");
                            un.setError("");
                        } catch (JSONException e) {
                            for (String x : credentials) {
                                if (x.isEmpty()) {
                                    result = 1;
                                    break;
                                }
                            }
                            if (credentials[2].charAt(2) != '-' && credentials[2].charAt(5) != '-') {
                                result = 2;
                            }
                            if (credentials[1].contains(" ")) {
                                result = 3;
                            }
                            if (!credentials[4].equals("Y") && !credentials[4].equals("N")) {
                                result = 4;
                            }

                            switch (result) {
                                case 1:
                                    name.setText("");
                                    age.setText("");
                                    addr.setText("");
                                    ld.setText("");
                                    pw.setText("");
                                    name.setHint("Fill in all fields !");
                                    name.setTextColor(Color.RED);
                                    break;
                                case 2:
                                    age.setText("");
                                    age.setText("Fill in correct format: DD-MM-YY !");
                                    age.setTextColor(Color.RED);
                                    break;
                                case 3:
                                    name.setText("");
                                    name.setHint("Only first name!");
                                    name.setTextColor(Color.RED);
                                    break;
                                case 4:
                                    ld.setText("");
                                    ld.setHint("Type 'Y' or 'N' !");
                                    ld.setTextColor(Color.RED);
                                    break;
                                case 0:
                                    String url = null;
                                    try {
                                        url = String.format("https://studev.groept.be/api/a24pt215/InsertNewUser/%s/%s/%s/%d/%s/%s/%s/%s/%s",
                                                URLEncoder.encode(usernameInput, StandardCharsets.UTF_8.toString()),
                                                URLEncoder.encode(nameInput, StandardCharsets.UTF_8.toString()),
                                                URLEncoder.encode(ageInput, StandardCharsets.UTF_8.toString()),
                                                0,
                                                URLEncoder.encode(addressInput, StandardCharsets.UTF_8.toString()),
                                                URLEncoder.encode(lenderInput, StandardCharsets.UTF_8.toString()),
                                                URLEncoder.encode(passwordInput, StandardCharsets.UTF_8.toString()),
                                                URLEncoder.encode(salt, StandardCharsets.UTF_8.toString()),
                                                URLEncoder.encode(token, StandardCharsets.UTF_8.toString())  // Add token here
                                        );
                                    } catch (UnsupportedEncodingException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                    signUpUser(url);
                                    break;
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUpActivity.this, "Network error! Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("DatabaseError", error.toString());
                    }
                });
        requestQueue.add(submitRequest);
    }
    public void signUpUser(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        editor.putString("Name", nameInput).apply();
                        editor.putString("Address_name", addressInput).apply();
                        editor.putString("Users_username", usernameInput).apply();
                        if (lenderInput.toLowerCase().trim().equals("y")) {
                            Intent i = new Intent(SignUpActivity.this, Lender.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUpActivity.this, "Network error! Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("ErrorWithLudo", error.getLocalizedMessage());
                    }
                });
        requestQueue.add(submitRequest);
    }
}