package com.example.makerlink.threads;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R;

import java.util.ArrayList;

public class ThreadListActivity extends AppCompatActivity {

    ArrayList<ThreadModel> threadItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thread_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.my_recycler);
        setUpThread();
        ThreadRecyclerViewAdapter threadAdapter = new ThreadRecyclerViewAdapter(this, threadItems);
        recyclerView.setAdapter(threadAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setUpThread() {
        String[] threadExamples = new String[3];
        threadExamples[0] = "Carpentry";
        threadExamples[1] = "Electronics";
        threadExamples[2] = "Mechanics";
        for (int i = 0; i < threadExamples.length; i++) {
            threadItems.add(new ThreadModel("How to split wood", R.drawable.letter_c, "Martin Pedata", "Carpentry", "01-01-2025"));
        }
    }
}