package com.example.makerlink.ui.exchange;

import android.os.Bundle;
import android.util.Log;
import com.example.makerlink.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.makerlink.databinding.FragmentExchangeBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ExchangeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentExchangeBinding binding;
    private GoogleMap myMap;
    private static final LatLng DEFAULT_LOCATION = new LatLng(-34, 151); // Sydney

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the ViewModel
        ExchangeViewModel dashboardViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);

        // Use ViewBinding to inflate the fragment layout
        binding = FragmentExchangeBinding.inflate(inflater, container, false);

        // Manually access the SupportMapFragment from the layout
        initMapFragment();

        return binding.getRoot();
    }

    private void initMapFragment() {
        // Manually retrieve the SupportMapFragment from the layout by ID
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag("MAP_FRAGMENT");

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mapFragment, "MAP_FRAGMENT")
                    .commit();
        }

        // Set the callback to notify when the map is ready
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        // Add a marker and move the camera to the default location
        myMap.addMarker(new MarkerOptions().position(DEFAULT_LOCATION).title("Marker in Sydney"));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 10f));

        // Enable map controls
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setScrollGesturesEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Release binding reference to avoid memory leaks
    }
}
