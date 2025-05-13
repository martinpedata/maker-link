package com.example.makerlink.navigation_pages.discovery;

import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.databinding.FragmentDiscoveryBinding;
import com.example.makerlink.threads.list.ThreadRecyclerModel;
import com.example.makerlink.threads.list.UserThreadRecyclerActivity;
import com.example.makerlink.threads.post.CreateThreadActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//TODO: DO SOME IMPLEMENTATION SUCH AS FILTERS. INTEGRATE WITH DATABASE.
public class DiscoveryFragment extends Fragment {

    private FragmentDiscoveryBinding binding;
    private int userID;
    private CardView threadsCard;
    private TextView threadsAmount;
    private RequestQueue requestQueue;

    public static DiscoveryFragment newInstance() {
        return new DiscoveryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiscoveryBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Return root view
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        threadsCard = binding.threadsCard;
        threadsAmount = binding.threadsAmount;

        threadsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UserThreadRecyclerActivity.class);
                startActivity(i);
            }
        });
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt("user_ID", -1);
        updateNumberOfThreads("https://studev.groept.be/api/a24pt215/RetreiveNumberOfUserThreads/" + userID);
    }

    public void updateNumberOfThreads(String requestURL) {
            requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject o = response.getJSONObject(0);
                                    int numThreads = o.getInt("Count(*)");
                                    threadsAmount.setText(Integer.toString(numThreads));
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println("json array problem");
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("inside onError");
                            Log.e("ErrorThreadCreazione", error.getLocalizedMessage());
                        }
                    }
            );
            requestQueue.add(submitRequest);
    }
}