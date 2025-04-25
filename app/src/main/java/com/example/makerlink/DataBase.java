package com.example.makerlink;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DataBase {
    private String name;
    private String age;
    private String username;
    private String password;
    private String email;

    private Context context;
    private RequestQueue requestQueue;
    private TextView txtResponse;
    public DataBase(Context context) {
        this.context = context;
    }
    public DataBase(){
        this.context = null;
    }

    public void retrieveName(String requestURL) {
        requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,

            new Response.Listener<JSONArray>()
            {
                @Override
                public void onResponse(JSONArray response)
                {
                    name = "";
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject o = response.getJSONObject(i);
                            name = o.getString("name");
                        }
                    }
                    catch (JSONException e) {
                    Log.e("Database", e.getMessage(), e);
                    }
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

    public void setName(String name) {
        this.name = name;
    }
    public String getName(String usernameInput) {
        retrieveName( "https://studev.groept.be/api/a24pt215/AllUserInfo/" + usernameInput);
        return name;
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
