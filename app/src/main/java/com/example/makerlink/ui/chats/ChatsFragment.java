package com.example.makerlink.ui.chats;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R;
import com.example.makerlink.databinding.FragmentChatsBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private RecyclerView recyclerView;
    private Community_Adapter chatadaptor;
    private List<Chat> chatList;
    private androidx.appcompat.widget.SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatsViewModel chatsViewModel =
                new ViewModelProvider(this).get(ChatsViewModel.class);

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        recyclerView = binding.getRoot().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatList = loadChats();
        chatadaptor = new Community_Adapter(chatList, chat -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chat_name", chat.getName());
            startActivity(intent);
        });
        recyclerView.setAdapter(chatadaptor);
        return binding.getRoot();
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
                chatadaptor.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chatadaptor.getFilter().filter(newText);
                return false;
            }
        });
    }
    private List<Chat> loadChats() {
        List<Chat> list = new ArrayList<>();
        list.add(new Chat("Ergi Durro"));
        list.add(new Chat("Group T"));
        list.add(new Chat("Martin Pedata"));
        list.add(new Chat("John"));
        list.add(new Chat("Jack"));
        list.add(new Chat("Mary"));
        list.add(new Chat("Ergi Durro"));
        list.add(new Chat("Group T"));
        list.add(new Chat("Martin Pedata"));
        list.add(new Chat("John"));
        list.add(new Chat("Jack"));
        list.add(new Chat("Mary"));
        list.add(new Chat("Ergi Durro"));
        list.add(new Chat("Group T"));
        list.add(new Chat("Martin Pedata"));
        list.add(new Chat("John"));
        list.add(new Chat("Jack"));
        list.add(new Chat("Mary"));
        list.add(new Chat("Ergi Durro"));
        list.add(new Chat("Group T"));
        list.add(new Chat("Martin Pedata"));
        list.add(new Chat("John"));
        list.add(new Chat("Jack"));
        list.add(new Chat("Mary"));
        list.add(new Chat("Ergi Durro"));
        list.add(new Chat("Group T"));
        list.add(new Chat("Martin Pedata"));
        list.add(new Chat("John"));
        list.add(new Chat("Jack"));
        list.add(new Chat("Mary"));
        return list;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}