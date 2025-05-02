package com.example.makerlink.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.makerlink.MainActivity;
import com.example.makerlink.databinding.FragmentHomeBinding;
import com.example.makerlink.threads.ThreadRecyclerActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("storeFilteredSearch", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        /// To see Threads

        ImageView discoveryImage = binding.DIYimage;
        ImageView mechanicsImage = binding.mechanicsPic;
        ImageView electronicsImage = binding.electronicsPic;
        ImageView cookingImage = binding.cookingPic;
        ImageView carpentryImage = binding.carpentryPic;

        discoveryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked image");

                editor.putInt("isFiltered", 0).apply();
                Intent i = new Intent(getActivity(), ThreadRecyclerActivity.class);
                startActivity(i);
            }
        });
        mechanicsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("isFiltered", 1).apply();
                Intent i = new Intent(getActivity(), ThreadRecyclerActivity.class);
                startActivity(i);
            }
        });
        electronicsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("isFiltered", 2).apply();
                Intent i = new Intent(getActivity(), ThreadRecyclerActivity.class);
                startActivity(i);
            }
        });
        cookingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("isFiltered", 3).apply();
                Intent i = new Intent(getActivity(), ThreadRecyclerActivity.class);
                startActivity(i);
            }
        });
        carpentryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("isFiltered", 4).apply();
                Intent i = new Intent(getActivity(), ThreadRecyclerActivity.class);
                startActivity(i);
            }
        });

        /// To see communities

        // To be implemented

    }

}