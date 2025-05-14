package com.example.makerlink.navigation_pages.exchange;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.makerlink.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InfoPage extends AppCompatActivity {

    private TextView name_of_lender;
    private TextView address_of_lender;
    private TextView rent;
    private TextView description;
    private Button order;
    private String tool;
    private EditText startEditText, endEditText;
    Calendar calendar = Calendar.getInstance();
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
        startEditText = findViewById(R.id.startlender);
        endEditText = findViewById(R.id.endlender);
        startEditText.setOnClickListener(v -> showDateTimePicker(startEditText));
        endEditText.setOnClickListener(v -> showDateTimePicker(endEditText));
        String name = getIntent().getStringExtra("name_of_user");
        String address = getIntent().getStringExtra("address_of_user");
        int rentofuser = getIntent().getIntExtra("rent_of_user", -1);
        String descriptionoflender = getIntent().getStringExtra("description_of_tool");
        tool = getIntent().getStringExtra("tool_of_user");
        name_of_lender.setText(name);
        address_of_lender.setText(address);
        rent.setText(String.valueOf(rentofuser));
        description.setText(descriptionoflender);
        order.setOnClickListener(v -> {

        });
    }
    private void showDateTimePicker(EditText targetEditText) {
        final Calendar tempCalendar = Calendar.getInstance();

        // Date picker first
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    tempCalendar.set(Calendar.YEAR, year);
                    tempCalendar.set(Calendar.MONTH, month);
                    tempCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Time picker after date is set
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                tempCalendar.set(Calendar.MINUTE, minute);

                                // Format the final datetime
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                targetEditText.setText(sdf.format(tempCalendar.getTime()));
                            },
                            tempCalendar.get(Calendar.HOUR_OF_DAY),
                            tempCalendar.get(Calendar.MINUTE),
                            true // is24HourView
                    );
                    timePickerDialog.show();
                },
                tempCalendar.get(Calendar.YEAR),
                tempCalendar.get(Calendar.MONTH),
                tempCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}