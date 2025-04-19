package com.example.makerlink.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.makerlink.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> items;
    private List<String> filteredItems;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container,false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        /**
         * SEARCH BAR SECTION
         * */

        super.onViewCreated(view, savedInstanceState);
        searchView = binding.searchBar;
        listView = binding.searchList;
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