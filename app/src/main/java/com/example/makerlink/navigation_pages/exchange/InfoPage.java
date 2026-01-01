package com.example.makerlink.navigation_pages.exchange;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.example.makerlink.access.Lender;
import com.example.makerlink.navigation_pages.chats.Chat;
import com.example.makerlink.navigation_pages.chats.ChatActivity;
import com.example.makerlink.navigation_pages.chats.Community_Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class InfoPage extends AppCompatActivity {

    private TextView name_of_lender;
    private TextView address_of_lender;
    private TextView rent;
    private TextView description;
    private Button order;
    private String tool;
    private EditText startEditText, endEditText;
    Calendar calendar = Calendar.getInstance();
    private List<Reservation> reservations;
    private RequestQueue requestQueue;
    private int lender_id;
    private int lendee_id;
    private SharedPreferences sharedPref;
    private int start_of_day;
    private int end_of_day;
    private ImageView image;
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
        image = findViewById(R.id.image_of_user);
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        lender_id = sharedPref.getInt("lender_id", -1);
        lendee_id = sharedPref.getInt("user_ID", -1);
        startEditText.setEnabled(false);
        endEditText.setEnabled(false);

        setUpReservations("https://studev.groept.be/api/a24pt215/getOrders/"+lender_id);
        String name = getIntent().getStringExtra("name_of_user");
        String image64 = getIntent().getStringExtra("imagePath");
        String address = getIntent().getStringExtra("address_of_user");
        int rentofuser = getIntent().getIntExtra("rent_of_user", -1);
        String descriptionoflender = getIntent().getStringExtra("description_of_tool");
        Bitmap bitmap = BitmapFactory.decodeFile(image64);
        image.setImageBitmap(bitmap);
        tool = getIntent().getStringExtra("tool_of_user");
        start_of_day = getIntent().getIntExtra("start_of_user", -1);
        end_of_day = getIntent().getIntExtra("end_of_user", -1);
        name_of_lender.setText(name);
        address_of_lender.setText(address);
        rent.setText(String.valueOf(rentofuser));
        description.setText(descriptionoflender);
        order.setOnClickListener(v -> {
            String startDateText = startEditText.getText().toString().trim();
            String endDateText = endEditText.getText().toString().trim();

            if (startDateText.isEmpty() || endDateText.isEmpty()) {
                Toast.makeText(InfoPage.this, "Please select both start and end times", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                if (!isTimeRangeAvailable(startDateText, endDateText, reservations)) {
                    Toast.makeText(this, "Selected time overlaps with an existing reservation", Toast.LENGTH_LONG).show();
                    return; // Don't proceed with the order
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            insertOrder("https://studev.groept.be/api/a24pt215/InsertOrder",lender_id, lendee_id, tool, Integer.parseInt(rent.getText().toString()), startDateText, endDateText);
            finish();
        });
    }
    private void showDateTimePicker(EditText targetEditText, List<Reservation> reservations) {
        final Calendar threshold = Calendar.getInstance();
        final Calendar tempCalendar = Calendar.getInstance();

        // Create and show a custom date picker dialog (instead of default DatePickerDialog)
        View customDateView = getLayoutInflater().inflate(R.layout.date_picker_dialog, null);
        DatePicker customDatePicker = customDateView.findViewById(R.id.customDatePicker);
        Button confirmDate = customDateView.findViewById(R.id.btnSetDate);

        // Set min date
        customDatePicker.setMinDate(threshold.getTimeInMillis());

        AlertDialog dateDialog = new AlertDialog.Builder(this)
                .setView(customDateView)
                .create();

        confirmDate.setOnClickListener(v -> {
            int year = customDatePicker.getYear();
            int month = customDatePicker.getMonth();
            int day = customDatePicker.getDayOfMonth();

            tempCalendar.set(Calendar.YEAR, year);
            tempCalendar.set(Calendar.MONTH, month);
            tempCalendar.set(Calendar.DAY_OF_MONTH, day);

            String[] availableHours = getAvailableHoursForDate(tempCalendar, reservations);

            if (availableHours.length == 0) {
                Toast.makeText(this, "No available hours on this date", Toast.LENGTH_SHORT).show();
                dateDialog.dismiss();
                return;
            }

            dateDialog.dismiss(); // Close the date dialog before showing the time dialog

            // Now show custom time picker
            View customTimeView = getLayoutInflater().inflate(R.layout.time_picker_dialog, null);
            NumberPicker hourPicker = customTimeView.findViewById(R.id.numberPickerHour);
            NumberPicker minutePicker = customTimeView.findViewById(R.id.numberPickerMinute);
            Button btnSetTime = customTimeView.findViewById(R.id.btnSetTime);

            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(availableHours.length - 1);
            hourPicker.setDisplayedValues(availableHours);
            hourPicker.setValue(0);

            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(0); // Only show 0 minute (hour-precision)
            minutePicker.setFormatter(value -> String.format("%02d", value));

            AlertDialog timeDialog = new AlertDialog.Builder(this)
                    .setView(customTimeView)
                    .create();

            btnSetTime.setOnClickListener(v2 -> {
                int hourIndex = hourPicker.getValue();
                int hour = Integer.parseInt(availableHours[hourIndex]);
                int minute = minutePicker.getValue();

                tempCalendar.set(Calendar.HOUR_OF_DAY, hour);
                tempCalendar.set(Calendar.MINUTE, minute);

                if (tempCalendar.compareTo(threshold) <= 0) {
                    targetEditText.setError("Please select a time after the current time");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                targetEditText.setText(sdf.format(tempCalendar.getTime()));
                timeDialog.dismiss();
            });

            timeDialog.show();
        });

        dateDialog.show(); // Only show this once after setup
    }


    // Your existing methods (unchanged)
    private String[] getAvailableHoursForDate(Calendar selectedDate, List<Reservation> reservations) {
        Set<Integer> unavailableHours = new HashSet<>();
        Calendar now = Calendar.getInstance(); // Current time

        boolean isToday = now.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR);

        for (int hour = start_of_day; hour <= end_of_day; hour++) {
            Calendar hourStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            hourStart.setTimeInMillis(selectedDate.getTimeInMillis());
            hourStart.set(Calendar.HOUR_OF_DAY, hour);
            hourStart.set(Calendar.MINUTE, 0);
            hourStart.set(Calendar.SECOND, 0);
            hourStart.set(Calendar.MILLISECOND, 0);

            Calendar hourEnd = (Calendar) hourStart.clone();
            hourEnd.add(Calendar.HOUR_OF_DAY, 1);

            for (Reservation reservation : reservations) {
                if (reservationOverlaps(reservation.startTime, reservation.endTime, hourStart, hourEnd)) {
                    unavailableHours.add(hour);
                    break;
                }
            }
        }

        List<String> available = new ArrayList<>();
        for (int i = start_of_day; i <= end_of_day; i++) {
            if (!unavailableHours.contains(i)) {
                if (isToday) {
                    // Skip past hours for today
                    if (i <= now.get(Calendar.HOUR_OF_DAY)) continue;
                }
                available.add(String.valueOf(i));
            }
        }

        return available.toArray(new String[0]);
    }

    private boolean reservationOverlaps(Calendar resStart, Calendar resEnd, Calendar dayStart, Calendar dayEnd) {
        return resStart.before(dayEnd) && resEnd.after(dayStart);
    }
    private boolean isTimeRangeAvailable(String startStr, String endStr, List<Reservation> reservations) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date startDate = sdf.parse(startStr);
        Date endDate = sdf.parse(endStr);

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        for (Reservation reservation : reservations) {
            if (startCal.before(reservation.endTime) && endCal.after(reservation.startTime)) {
                // Overlap detected
                return false;
            }
        }
        return true; // No overlaps
    }
    public void setUpReservations(String requestURL) {
        if (reservations == null) {
            reservations = new ArrayList<>();
        } else {
            reservations.clear();
        }
        requestQueue = Volley.newRequestQueue(InfoPage.this);

        // Make the GET request to retrieve community names the user is part of
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject communityObject = response.getJSONObject(i);

                                String startStr = communityObject.getString("start_rent");
                                String endStr = communityObject.getString("end_rent");

                                Date startDate = sdf.parse(startStr);
                                Date endDate = sdf.parse(endStr);

                                Calendar startCal = Calendar.getInstance();
                                startCal.setTime(startDate);

                                Calendar endCal = Calendar.getInstance();
                                endCal.setTime(endDate);

                                reservations.add(new Reservation(startCal, endCal));
                            }
                            runOnUiThread(() -> {
                                startEditText.setEnabled(true);
                                endEditText.setEnabled(true);
                                startEditText.setOnClickListener(v -> showDateTimePicker(startEditText, reservations));
                                endEditText.setOnClickListener(v -> showDateTimePicker(endEditText, reservations));
                            });

                        } catch (JSONException | ParseException e) {
                            Log.e("Error", "Error processing JSON response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Error fetching communities", error);
                    }
                });

        // Add the request to the request queue
        requestQueue.add(submitRequest);
    }
    public void insertOrder(String url, int lender_id, int lendee_id, String tooltype, int rent, String start_date, String end_date) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("MessagePost", "Response: " + response);
                    Toast.makeText(InfoPage.this, "Order Added", Toast.LENGTH_SHORT).show();


                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("VolleyError", "Error: " + errorMsg);
                        Toast.makeText(InfoPage.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("VolleyError", "Unknown error occurred");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("lender", String.valueOf(lender_id));  // must match backend expected name
                params.put("lendee", String.valueOf(lendee_id));
                params.put("price", String.valueOf(rent));
                params.put("tool",tooltype);
                params.put("startrent", start_date);
                params.put("endrent", end_date);
                return params;
            }
        };

        Volley.newRequestQueue(InfoPage.this).add(stringRequest);
    }
    public Bitmap base64ToBitMap(String b64String){
        byte[] imageBytes = Base64.decode( b64String, Base64.NO_WRAP );
        Bitmap bitmap = BitmapFactory.decodeByteArray( imageBytes, 0, imageBytes.length );
        return bitmap;
    }
}