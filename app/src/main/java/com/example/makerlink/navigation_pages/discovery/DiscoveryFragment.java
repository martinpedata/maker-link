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
import com.example.makerlink.threads.post.CreateThreadActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

//TODO: DO SOME IMPLEMENTATION SUCH AS FILTERS. INTEGRATE WITH DATABASE.
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

}