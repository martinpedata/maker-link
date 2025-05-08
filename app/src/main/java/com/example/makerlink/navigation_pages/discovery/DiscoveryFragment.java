package com.example.makerlink.navigation_pages.discovery;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.makerlink.databinding.FragmentDiscoveryBinding;
import com.example.makerlink.threads.CreateThreadActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryFragment extends Fragment {

    private FragmentDiscoveryBinding binding;
    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> items;
    private List<String> filteredItems;
    private FloatingActionButton createThreadButton;

    private DiscoveryViewModel mViewModel;

    public static DiscoveryFragment newInstance() {
        return new DiscoveryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiscoveryBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Return root view
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        /// Define variables, tools, adapters, views, etc...

        super.onViewCreated(view, savedInstanceState);
        searchView = binding.searchBar;
        listView = binding.searchList;
        createThreadButton = binding.addThread;


        mViewModel = new ViewModelProvider(this).get(DiscoveryViewModel.class);
        items = new ArrayList<>();
        items.add("Apples");
        items.add("Bananas");
        items.add("Carrots");
        items.add("Donuts");
        items.add("Eggs");
        items.add("Fish");


        filteredItems = new ArrayList<>();
//        To refer to a Fragment's Activity, use requireActivity() or getContext()
        adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, filteredItems);
        listView.setAdapter(adapter);
        listView.setVisibility(View.GONE);


        /// CREATE-THREAD LOGIC

        createThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreateThreadActivity.class);
                startActivity(i);
            }
        });

        /// SEARCH VIEW LOGIC

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
                listView.setVisibility(View.VISIBLE);
                filteredItems.clear();

                if (newText.isEmpty()) {
                    listView.setVisibility(View.GONE); // Hide again if search is cleared
                } else {
                    for (String item : items) {
                        if (item.toLowerCase().contains(newText.toLowerCase())) {
                            filteredItems.add(item);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                return true;
            }
        });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}