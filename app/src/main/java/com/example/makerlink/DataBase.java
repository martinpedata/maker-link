package com.example.makerlink;

import android.content.Context;
import android.util.Log;

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

    public DataBase(Context context) {
        this.context = context;
    }
    public DataBase(){
        this.context = null;
    }

    private String makeGETRequest(String urlName) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Error: HTTP Response Code " + responseCode);
                return "";
            }

            try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            }
            conn.disconnect();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCurrentString(String str) {
        String jsonResponse = makeGETRequest(str);
        if (jsonResponse.isEmpty()) {
            System.out.println("Error: Empty response from API");
            return ""; // Error case
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String status = jsonObject.getString("Sensor_stat");  // Adjust key name based on actual JSON response
                return status;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCurrentDate (String url) {
        String jsonResponse = makeGETRequest(url);
        if (jsonResponse.isEmpty()) {
            System.out.println("Error: Empty response from API");
            return ""; // Error case
        }
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String status = jsonObject.getString("recorded_at");  // Adjust key name based on actual JSON response
                return status;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public void clearHistoryDB() {
        Log.d("HistoryFragment", "Attempting to clear history DB");
        try {
            URL url = new URL("https://studev.groept.be/api/a24ib2team406/ClearTable");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            Log.d("HistoryFragment", "API response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("HistoryFragment", "Table cleared successfully");
            } else {
                Log.e("HistoryFragment", "Failed to clear table. Response code: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            Log.e("HistoryFragment", "Error clearing table: " + e.getMessage(), e);
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
