package com.example.makerlink.access;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SignUpActivity extends AppCompatActivity {
    private TextView signUpText;
    private SharedPreferences sharedPref;
    private String nameInput;
    private RequestQueue requestQueue;
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

    public void goToHome(View view) throws UnsupportedEncodingException {

        EditText pw = findViewById(R.id.password);
        String passwordInput = pw.getText().toString();
        EditText un = findViewById(R.id.username);
        String usernameInput = un.getText().toString();
        EditText em = findViewById(R.id.email);
        String emailInput = em.getText().toString();
        EditText age = findViewById(R.id.age);
        String ageInput = age.getText().toString();
        EditText name = findViewById(R.id.name);
        nameInput = name.getText().toString();

        ///  CHANGE THIS BELOW:

        String address = "MariaTheresiastraat 82";
        String lender = "yes";
        int locationID = 0;

        SignUpValidity validity = new SignUpValidity(ageInput,nameInput,emailInput,passwordInput,usernameInput);
        switch (validity.checkValidity()) {
            case 0:
                if (!usernameInput.isEmpty() && !passwordInput.isEmpty()) {
                    String url = String.format("https://studev.groept.be/api/a24pt215/InsertNewUser/%s/%s/%s/%s/%d/%s/%s/%d/%s",
                            URLEncoder.encode(usernameInput, StandardCharsets.UTF_8.toString()),
                            URLEncoder.encode(nameInput, StandardCharsets.UTF_8.toString()),
                            URLEncoder.encode(emailInput, StandardCharsets.UTF_8.toString()),
                            URLEncoder.encode(ageInput, StandardCharsets.UTF_8.toString()),
                            0,
                            URLEncoder.encode(address, StandardCharsets.UTF_8.toString()),
                            URLEncoder.encode(lender, StandardCharsets.UTF_8.toString()),
                            locationID,
                            URLEncoder.encode(passwordInput, StandardCharsets.UTF_8.toString()));
                    signUpUser(url);
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
    public void signUpUser(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        System.out.println("We're in signupUser");
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("We're in onResponse");
                        editor.putString("Name", nameInput).apply();

                        /// GO to navigation template

                        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SIGNUP", "Status Code: " + error.networkResponse.statusCode);
                        Log.e("SIGNUP", "Response Data: " + new String(error.networkResponse.data));
                    }
                }
        );
        requestQueue.add(submitRequest);
    }
}