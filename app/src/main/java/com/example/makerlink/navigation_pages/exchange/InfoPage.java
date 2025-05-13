package com.example.makerlink.navigation_pages.exchange;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.makerlink.R;

public class InfoPage extends AppCompatActivity {

    private TextView name_of_lender;
    private TextView address_of_lender;
    private TextView rent;
    private TextView description;
    private Button order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name_of_lender = findViewById(R.id.nameLender);
        address_of_lender = findViewById(R.id.addressLender);
        rent = findViewById(R.id.rentlender);
        description = findViewById(R.id.descriptionlender);
        order = findViewById(R.id.orderButton);
        String name = getIntent().getStringExtra("name_of_user");
        String address = getIntent().getStringExtra("address_of_user");
        int rentofuser = getIntent().getIntExtra("rent_of_user", -1);
        String descriptionoflender = getIntent().getStringExtra("description_of_tool");
        name_of_lender.setText(name);
        address_of_lender.setText(address);
        rent.setText(String.valueOf(rentofuser));
        description.setText(descriptionoflender);
    }
}