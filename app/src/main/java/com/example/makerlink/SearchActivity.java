package com.example.makerlink;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> items;
    private List<String> filteredItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);

        // Sample data
        items = new ArrayList<>();
        items.add("Apples");
        items.add("Bananas");
        items.add("Carrots");
        items.add("Donuts");
        items.add("Eggs");
        items.add("Fish");

        filteredItems = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredItems);
        listView.setAdapter(adapter);
        listView.setVisibility(View.GONE);

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
}