package com.example.makerlink.navigation_pages.discovery;

import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.animation.ValueAnimator;
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
import com.example.makerlink.gamification.AccomplishmentsActivity;
import com.example.makerlink.threads.list.ThreadRecyclerModel;
import com.example.makerlink.threads.list.UserThreadRecyclerActivity;
import com.example.makerlink.threads.post.CreateThreadActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryFragment extends Fragment {

    private FragmentDiscoveryBinding binding;
    private int userID;
    private CardView threadsCard;
    private CardView pointsCard;
    private CardView ordersCard;
    private TextView threadsAmount;
    private TextView pointsAmount;
    private TextView ordersAmount;
    private RequestQueue requestQueue;
    private SharedPreferences.Editor editor;

    public static DiscoveryFragment newInstance() {
        return new DiscoveryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiscoveryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        threadsCard = binding.threadsCard;
        pointsCard = binding.pointsCard;
        ordersCard = binding.orderCard;

        threadsAmount = binding.threadsAmount;
        pointsAmount = binding.pointsScored;
        ordersAmount = binding.orderAmount;

        threadsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UserThreadRecyclerActivity.class);
                startActivity(i);
            }
        });

        pointsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AccomplishmentsActivity.class);
                startActivity(i);
            }
        });
        //TODO:implement orders card.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userID = sharedPreferences.getInt("user_ID", -1);
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveNumberOfThreads("https://studev.groept.be/api/a24pt215/RetreiveNumberOfUserThreads/" + userID);
        retrieveNumberOfPoints("https://studev.groept.be/api/a24pt215/RetrievePoints/" + userID);
        retrieveNumberOfOrders("https://studev.groept.be/api/a24pt215/RetrieveNumberOfOrders/" + userID + "/" + userID);
    }
    private void animateNumbers(TextView targetView, int start, int end, long duration) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(duration); // Duration in milliseconds
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            targetView.setText(String.valueOf(animatedValue)); ///TARGET VIEW IS A LOCAL PARAM
        });
        animator.start();
    }

    public void retrieveNumberOfThreads(String requestURL) {
            requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject o = response.getJSONObject(0);
                                int numThreads = o.getInt("Count(*)");
                                animateNumbers(threadsAmount, 0, numThreads, 1000);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                                System.out.println("json array problem");
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

    public void retrieveNumberOfPoints(String requestURL) {
        requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject o = response.getJSONObject(0);
                            int numPoints = o.getInt("points");
                            editor.putInt("points",numPoints).apply();
                            animateNumbers(pointsAmount, 0, numPoints, 2000);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("json array problem");
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

   public void  retrieveNumberOfOrders (String requestURL) {
        requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject o = response.getJSONObject(0);
                            int numOrders = o.getInt("Count(*)");
                            animateNumbers(ordersAmount, 0, numOrders, 1000);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("json array problem");
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