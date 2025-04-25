package com.example.makerlink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataBase {
    private String name;
    private String age;
    private String username;
    private String temporaryName;
    private String password;
    private String email;

    private Context context;
    private RequestQueue requestQueue;
    private TextView txtResponse;
    public DataBase(Context context) {
        this.context = context;
        name = "";
    }
    public DataBase(){
        this.context = null;
    }

    public void insertUser(String requestURL) {
        requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.POST, requestURL, null,

                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        System.out.println("success");
                    }
                },

                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txtResponse.setText(error.getLocalizedMessage());
                    }
                }
        );
        requestQueue.add(submitRequest);
    }

    public void signUpUser(String nameInput, String usernameInput, String email, String dateOfBirth, int points, String address, String lender, int locationID, String password) {
        @SuppressLint("DefaultLocale") String url = String.format("https://studev.groept.be/api/a24pt215/InsertNewUser/%s/%s/%s/%s/%d/%s/%s/%d/%s",
                usernameInput,
                nameInput,
                email,
                dateOfBirth,
                points,
                address,
                lender,
                locationID,
                password);
        insertUser(url);
    }
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
