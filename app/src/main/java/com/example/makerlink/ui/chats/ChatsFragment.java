package com.example.makerlink.ui.chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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