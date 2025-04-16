package com.example.makerlink.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.makerlink.MainActivity;
import com.example.makerlink.R;
import com.example.makerlink.SearchActivity;
import com.example.makerlink.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        Button btnOpenSearch = binding.getRoot().findViewById(R.id.btnOpenSearch);
        btnOpenSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
        return binding.getRoot();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}