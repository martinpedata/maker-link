package com.example.makerlink.navigation_pages.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Lender_change_info_page extends AppCompatActivity {
    private TextInputEditText address;
    private TextInputEditText tool;
    private TextInputEditText rent;
    private TextInputEditText description;
    private TextInputEditText starthour;
    private TextInputEditText endhour;
    private ImageView imagetool;
    private Bitmap bitmap;
    private String image64;
    private int location_id;
    private Button modifybutton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lender_change_info_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        address = findViewById(R.id.address_profile_fragment);
        tool = findViewById(R.id.tooltype_change_fragment);
        rent = findViewById(R.id.rent_change_profile);
        description = findViewById(R.id.lender_description_fragment);
        starthour = findViewById(R.id.start_time_fragment);
        endhour = findViewById(R.id.end_time_fragment);
        imagetool = findViewById(R.id.thumbnailImage);
        ImageView placeholderThumbnail = findViewById(R.id.placeholderThumbnail);
        String address1 = getIntent().getStringExtra("address_of_user");
        String tool1 = getIntent().getStringExtra("tool_of_user");
        int rent1 = getIntent().getIntExtra("rent_of_user", -1);
        String description1 = getIntent().getStringExtra("description_of_tool");
        int startday = getIntent().getIntExtra("start_of_user", -1);
        int endday = getIntent().getIntExtra("end_of_user", -1);
        image64 = getIntent().getStringExtra("imagePath");
        location_id = getIntent().getIntExtra("location_id", -1);
        address.setText(address1);
        tool.setText(tool1);
        rent.setText(String.valueOf(rent1));
        description.setText(description1);
        starthour.setText(String.valueOf(startday));
        endhour.setText(String.valueOf(endday));
        bitmap = BitmapFactory.decodeFile(image64);
        imagetool.setImageBitmap(bitmap);
        placeholderThumbnail.setVisibility(View.INVISIBLE);
        requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(this);
        modifybutton = findViewById(R.id.savebutton);
        modifybutton.setOnClickListener(v -> {
            String addresstext = address.getText().toString();
            String tooltype = tool.getText().toString();
            String rentlender = rent.getText().toString();
            String description2 = description.getText().toString();
            String startlender = starthour.getText().toString();
            String endlender = endhour.getText().toString();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            image64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            updateLender(addresstext, tooltype, rentlender, description2, startlender, endlender, image64, location_id);
            finish();
        });
    }

    public Bitmap base64ToBitMap(String b64String){
        byte[] imageBytes = Base64.decode( b64String, Base64.NO_WRAP );
        Bitmap bitmap = BitmapFactory.decodeByteArray( imageBytes, 0, imageBytes.length );
        return bitmap;
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
                imagetool.setImageBitmap(bitmap);


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
    private void updateLender(String address, String tooltype, String rent, String description, String start_time, String end_time, String base64, int location_id) {
        String url = "https://studev.groept.be/api/a24pt215/UpdateLenderInfo";  // Replace with your actual API endpoint

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("UpdateUser", "Response: " + response);
                    // Handle success
                    Log.d("UpdateUser", "Successfully updated User");
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("UpdateUser", "Error: " + errorMsg);
                        Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("UpdateUser", "Unknown error occurred");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("addres", address);
                params.put("tool", tooltype);
                params.put("ren", rent);
                params.put("des", description);
                params.put("start", start_time);
                params.put("endi", end_time);
                params.put("imag", base64);
                params.put("locval", String.valueOf(location_id));
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }


}