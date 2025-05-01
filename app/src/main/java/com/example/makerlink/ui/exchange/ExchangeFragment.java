package com.example.makerlink.ui.exchange;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.makerlink.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.databinding.FragmentExchangeBinding;
import com.example.makerlink.ui.discovery.DiscoveryViewModel;
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
import android.location.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExchangeFragment extends Fragment implements OnMapReadyCallback {
    private FragmentExchangeBinding binding;
    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private List<User> newUser;

    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> adapterlist;
    private List<String> items;
    private List<String> filteredItems;

    private DiscoveryViewModel mViewModel;
    private FusedLocationProviderClient fusedLocationClient;

    private String selectedSearchItem = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the ViewModel
        ExchangeViewModel dashboardViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);

        // Use ViewBinding to inflate the fragment layout
        binding = FragmentExchangeBinding.inflate(inflater, container, false);
        recyclerView = binding.getRoot().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = loadUsers();
        adapter = new UserAdapter(new ArrayList<>(userList));
        recyclerView.setAdapter(adapter);
        initMapFragment();

        return binding.getRoot();
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
        int id_id = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);

        TextView searchText = searchView.findViewById(id_id);

        if (searchText != null) {
            searchText.setTextColor(Color.BLACK);
            searchText.setHintTextColor(Color.GRAY); // Optional
        }
        listView = binding.searchList;
        mViewModel = new ViewModelProvider(this).get(DiscoveryViewModel.class);
        items = new ArrayList<>();
        items.add("Basic Tools");
        items.add("Electronics");
        items.add("Mechanics");
        items.add("Carpentry");
        items.add("Cooking");
        items.add("Pluming");
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
        if (mMap == null) return;
        clearExistingMarkers();
        Geocoder geocoder = new Geocoder(requireContext());

        // Filter users based on selectedSearchItem (tool type)
        for (User user : userList) {
            if (user.getTool().equalsIgnoreCase(selectedSearchItem)) {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(user.getAddress(), 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                        // Add a marker with user name as title
                        mMap.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title(user.getName()))
                                .setTag(user); // Save the user object for later
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
    private List<User> loadUsers() {
        // Dummy users for example
        List<User> list = new ArrayList<>();
        list.add(new User("Ergi Durro", "Paul van Ostaijenlaan, 21", "+32460946315", 100,"Basic Tools"));
        list.add(new User("Group T", "Andreas Vesaliusstraat 13, 3000", "+12345678", 50, "Electronics"));
        list.add(new User("Martin Pedata", "Maria Theresiastraat 84, 3000 Leuven", "+238576943", 30,"Basic Tools"));
        list.add(new User("John", "Edward van Evenstraat 4, 3000 Leuven", "+4985745", 60, "Pluming"));
        list.add(new User("Jack", "Alfons Smetsplein 7, 3000 Leuven", "+48769556", 45,"Cooking"));
        list.add(new User("Mary", "Bondgenotenlaan 20, 3000 Leuven", "23456789", 120, "Carpentry"));
        return list;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Release binding reference to avoid memory leaks
    }
}