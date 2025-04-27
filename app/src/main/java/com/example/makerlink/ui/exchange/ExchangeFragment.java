package com.example.makerlink.ui.exchange;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import com.example.makerlink.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.location.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentExchangeBinding binding;
    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    private FusedLocationProviderClient fusedLocationClient;

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
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
        Geocoder geocoder = new Geocoder(requireContext());

        // Add a marker and move the camera to the default location

        for (User user : userList) {
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
            adapter.updateList(new ArrayList<>(userList)); // Click on map -> show all users again
        });

        // Enable map controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
    }
    private List<User> loadUsers() {
        // Dummy users for example
        List<User> list = new ArrayList<>();
        list.add(new User("Ergi Durro", "Paul van Ostaijenlaan, 21", "+32460946315", 100));
        list.add(new User("Group T", "Andreas Vesaliusstraat 13, 3000", "+12345678", 50));
        list.add(new User("Martin Pedata", "Maria Theresiastraat 84, 3000 Leuven", "+238576943", 30));
        list.add(new User("John", "Edward van Evenstraat 4, 3000 Leuven", "+4985745", 60));
        list.add(new User("Jack", "Alfons Smetsplein 7, 3000 Leuven", "+48769556", 45));
        list.add(new User("Mary", "Bondgenotenlaan 20, 3000 Leuven", "23456789", 120));
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
