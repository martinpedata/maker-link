package com.example.makerlink.navigation_pages.exchange;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.Manifest;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.databinding.FragmentExchangeBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Address;
import android.location.Geocoder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ExchangeFragment extends Fragment implements OnMapReadyCallback {
    private FragmentExchangeBinding binding;
    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;

    private List<User> fulluserList;
    private List<User> newUser;

    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> adapterlist;
    private List<String> items;
    private List<String> filteredItems;

    private FusedLocationProviderClient fusedLocationClient;

    private String selectedSearchItem = "";
    private RequestQueue requestQueue;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int own_id;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the ViewModel
        ExchangeViewModel dashboardViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);

        // Use ViewBinding to inflate the fragment layout
        binding = FragmentExchangeBinding.inflate(inflater, container, false);
        recyclerView = binding.getRoot().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initMapFragment();

        return binding.getRoot();
    }
    @Override
    public void onResume() {
        super.onResume();
        sharedPref = requireContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        own_id = sharedPref.getInt("user_ID", -1);
        editor = sharedPref.edit();
        setUpLenders("https://studev.groept.be/api/a24pt215/RetrieveLenderInfo");
    }
    private void initMapFragment() {


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag("MAP_FRAGMENT");

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mapFragment, "MAP_FRAGMENT")
                    .commit();
        }


        // Set the callback to notify when the map is ready
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        searchView = binding.searchBar;
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.BLACK);
        searchEditText.setHintTextColor(Color.GRAY);

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);

        if (searchIcon != null) {
            searchIcon.setImageResource(R.drawable.icon_search);
            searchIcon.setImageTintList(null);
            searchIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }
        ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeIcon != null) {
            closeIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }
        listView = binding.searchList;
        filteredItems = new ArrayList<>();
//        To refer to a Fragment's Activity, use requireActivity() or getContext()
        adapterlist = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, filteredItems){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.BLACK);
                return view;
            }
        };
        listView.setAdapter(adapterlist);
        listView.setVisibility(View.GONE);

//        To make sure the entire search bar is touchable and not just icon. Also to make the keyboard appear upon touch

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

//        Search through the list

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // We handle everything on text change
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                selectedSearchItem = newText.trim(); // Update the selected search item
                filteredItems.clear(); // Clear previous filters

                // Check if the search text is empty
                if (newText.isEmpty()) {
                    listView.setVisibility(View.GONE); // Hide ListView if the search is empty
                } else {
                    // Filter items and add matching items to filteredItems
                    for (String item : items) {
                        if (item.toLowerCase().contains(newText.toLowerCase())) {
                            filteredItems.add(item);
                        }
                    }
                    // Show the ListView when there are items to display
                    if (!filteredItems.isEmpty()) {
                        listView.setVisibility(View.VISIBLE);
                    } else {
                        listView.setVisibility(View.GONE); // Hide if no items match
                    }
                    adapterlist.notifyDataSetChanged();
                }

                updateMapMarkers(); // Update map markers when the search changes
                return true;
            }
        });
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            selectedSearchItem = filteredItems.get(position);
            searchView.setQuery(selectedSearchItem, false);
            listView.setVisibility(View.GONE);
            updateMapMarkers(); // Update map when a search item is selected
        });

    }
    private void clearExistingMarkers() {
        if (mMap != null) {
            mMap.clear();
        }
    }
    private void updateMapMarkers() {
        if (mMap == null || userList == null) return; // Prevent crash if userList isn't ready
        clearExistingMarkers();
        Geocoder geocoder = new Geocoder(requireContext());

        for (User user : userList) {
            if (user.getTool().equalsIgnoreCase(selectedSearchItem)) {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(user.getAddress(), 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                        mMap.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title(user.getName()))
                                .setTag(user);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        newUser = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);

        // Move camera to user's location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                    }
                });

        updateMapMarkers();

        mMap.setOnMarkerClickListener(marker -> {
            User user = (User) marker.getTag();
            if (user != null) {
                List<User> singleUser = new ArrayList<>();
                singleUser.add(user);
                adapter.updateList(singleUser);
            }
            return false; // allow default behavior too (camera move)
        });
        mMap.setOnMapClickListener(latLng -> {
            updateMapMarkers();
            adapter.updateList(new ArrayList<>(userList));
        });

        // Enable map controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
    }
    public void setUpLenders(String requestURL) {
        if (userList == null) {
            userList = new ArrayList<>();
        } else {
            userList.clear();
        }
        items = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            // Iterate over the response array to get each community's data
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject communityObject = response.getJSONObject(i);

                                String image = communityObject.getString("image_res");
                                int id = communityObject.getInt("user_id");
                                editor.putInt("lender_id", id).apply();
                                String name = communityObject.getString("name");
                                String address = communityObject.getString("address");
                                String tool = communityObject.getString("tooltype");
                                int rent = communityObject.getInt("rent");
                                String description = communityObject.getString("description");
                                int startofday = communityObject.getInt("start_time");
                                int endofday = communityObject.getInt("end_time");

                                if (id != own_id){// Add the community to the chatList
                                    userList.add(new User(name, address,rent,tool, description, startofday, endofday, image));
                                    items.add(tool);
                                }
                            }
                            HashSet<String> hset = new HashSet<String>(items);
                            items = new ArrayList<>(hset);
                            // Now, set the adapter with the list of communities
                            if (adapter == null) {
                                // First-time setup of the adapter
                                adapter = new UserAdapter(userList);

                                // Set up RecyclerView with the adapter and layout manager
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            } else {
                                recyclerView.scrollToPosition(0);
                                adapter.notifyDataSetChanged(); // Update the RecyclerView
                                updateMapMarkers(); // Update the RecyclerView
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "Location permission granted");
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);

                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(location -> {
                                    if (location != null) {
                                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                                        Log.d("Permissions", "Moved to current location: " + currentLatLng);
                                    } else {
                                        Log.d("Permissions", "Location is null after granting permission");
                                    }
                                });
                    } else {
                        Log.d("Permissions", "mMap is null");
                    }
                }
            } else {
                Log.d("Permissions", "Location permission denied");
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Release binding reference to avoid memory leaks
    }
}