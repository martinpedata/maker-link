package com.example.makerlink.navigation_pages.chats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.example.makerlink.databinding.FragmentChatsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private RecyclerView recyclerView;
    private Community_Adapter chatadaptor;
    private List<Chat> chatList;
    private androidx.appcompat.widget.SearchView searchView;
    private RequestQueue requestQueue;
    private FloatingActionButton addbutton;
    private int UserID;
    private SharedPreferences sharedPrefer;
    private SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatsViewModel chatsViewModel =
                new ViewModelProvider(this).get(ChatsViewModel.class);

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        recyclerView = binding.getRoot().findViewById(R.id.recyclerView);
        SharedPreferences sharedPref = requireContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        UserID = sharedPref.getInt("user_ID", -1);
        addbutton = binding.getRoot().findViewById(R.id.fab);
        addbutton.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AddCommunity.class);
            startActivity(i);
        });
        return binding.getRoot();
    }
    @Override
    public void onResume() {
        super.onResume();
        setUpCommunity("https://studev.groept.be/api/a24pt215/GetCommunityName/" + UserID);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = binding.searchBar;
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK);  // Set the text color to black
            searchEditText.setHintTextColor(Color.BLACK);  // Set the hint color to black
        }

        // Change color of the magnifying glass icon to black
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) {
            searchIcon.setImageResource(R.drawable.icon_search);
            searchIcon.setImageTintList(null);
            searchIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);  // Change icon color to black
        }

        // Change color of the close button to black
        ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeIcon != null) {
            closeIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);  // Change icon color to black
        }
        searchView.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        });
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (chatadaptor != null) {
                    chatadaptor.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (chatadaptor != null) {
                    chatadaptor.getFilter().filter(newText);
                }
                return false;
            }
        });
    }
    public void setUpCommunity(String requestURL) {
        if (chatList == null) {
            chatList = new ArrayList<>();
        } else {
            chatList.clear();
        }
        requestQueue = Volley.newRequestQueue(getContext());

        // Make the GET request to retrieve community names the user is part of
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            // Iterate over the response array to get each community's data
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject communityObject = response.getJSONObject(i);

                                // Get the community name and community_id from the response
                                String name = communityObject.getString("name");
                                int communityId = communityObject.getInt("community");

                                // Add the community to the chatList
                                chatList.add(new Chat(name, communityId));
                            }

                            // Now, set the adapter with the list of communities
                            if (chatadaptor == null) {
                                // First-time setup of the adapter
                                chatadaptor = new Community_Adapter(chatList, chat -> {
                                    // Pass the community name and ID to the next screen
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("chat_name", chat.getName());
                                    intent.putExtra("community_id", chat.getId()); // Pass the community ID as well
                                    startActivity(intent);
                                });

                                // Set up RecyclerView with the adapter and layout manager
                                recyclerView.setAdapter(chatadaptor);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            } else {
                                recyclerView.scrollToPosition(0);
                                chatadaptor.notifyDataSetChanged(); // Update the RecyclerView
                            }

                        } catch (JSONException e) {
                            Log.e("Error", "Error processing JSON response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Error fetching communities", error);
                    }
                });

        // Add the request to the request queue
        requestQueue.add(submitRequest);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}