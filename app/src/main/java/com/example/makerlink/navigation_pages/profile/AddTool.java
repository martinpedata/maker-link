package com.example.makerlink.navigation_pages.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.example.makerlink.access.Lender;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddTool extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private TextView rent;
    private TextView tooltype;
    private TextView description;
    private TextView startofday;
    private TextView endofday;
    private Button homebutton;
    private TextView address;
    private ImageView thumbnailImage;
    private ImageView placeholderThumbnail;
    private TextView clickMe;
    private Bitmap bitmap;
    private String base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_tool);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        int user_id = sharedPref.getInt("user_ID", -1);
        rent = findViewById(R.id.rent);
        tooltype = findViewById(R.id.tool);
        description = findViewById(R.id.description_of_tools);
        startofday = findViewById(R.id.starttime);
        endofday = findViewById(R.id.endtime);
        address = findViewById(R.id.addressoftool);
        thumbnailImage = findViewById(R.id.thumbnailImage);
        placeholderThumbnail = findViewById(R.id.placeholderThumbnail);
        clickMe = findViewById(R.id.clickMeThumbnail);
        homebutton = findViewById(R.id.homeButton);
        homebutton.setOnClickListener(v -> {
            String rent1 = rent.getText().toString();
            String tool1 = tooltype.getText().toString();
            String description1 = description.getText().toString();
            String start1 = startofday.getText().toString();
            String end1 = endofday.getText().toString();
            String address1 = address.getText().toString();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            base64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            insertLender("https://studev.groept.be/api/a24pt215/InsertLender", user_id, address1, tool1, rent1, description1, start1,end1, base64);
            finish();
        });
    }
    public void insertLender(String url, int user_id, String address_val, String tooltype, String rent, String description, String startday, String endday, String image) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("MessagePost", "Response: " + response);
                    Toast.makeText(this, "Tool created!", Toast.LENGTH_SHORT).show();


                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("VolleyError", "Error: " + errorMsg);
                        Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("VolleyError", "Unknown error occurred");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("addressval", address_val);  // must match backend expected name
                params.put("toolval", tooltype);
                params.put("userval", String.valueOf(user_id));
                params.put("rentval",rent);
                params.put("descriptionval", description);
                params.put("startofday", startday);
                params.put("endofday", endday);
                params.put("image", image);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
    public void onBtnPickClicked(View caller)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);

        //this line will start the new activity and will automatically run the callback method below when the user has picked an image
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 111);
    }

    /// Converts chosen image into a bitmap
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Rescale the bitmap to 300px wide (avoid storing large images!)
                bitmap = getResizedBitmap( bitmap, 450 );

                //Setting background image of thumbnail
                thumbnailImage.setImageBitmap(bitmap);
                clickMe.setVisibility(View.INVISIBLE);
                placeholderThumbnail.setVisibility(View.INVISIBLE);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /// Helper method
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scale = ((float) newWidth) / width;

        // We create a matrix to transform the image
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create the new bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}